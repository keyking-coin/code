//----------------------------------------------
//            NGUI: Next-Gen UI kit
// Copyright © 2011-2014 Tasharen Entertainment
//----------------------------------------------

using UnityEngine;
using System.Collections.Generic;

/// <summary>
/// This is a script used to keep the game object scaled to 2/(Screen.height).
/// If you use it, be sure to NOT use UIOrthoCamera at the same time.
/// </summary>

[ExecuteInEditMode]
[AddComponentMenu("NGUI/UI/Root")]
public class UIRoot : MonoBehaviour
{
	static public List<UIRoot> list = new List<UIRoot>();

	/// <summary>
	/// List of all UIRoots present in the scene.
	/// </summary>

	public enum Scaling
	{
		PixelPerfect,
		FixedSize,
		FixedSizeOnMobiles,
	}

	/// <summary>
	/// Type of scaling used by the UIRoot.
	/// </summary>

	public Scaling scalingStyle = Scaling.PixelPerfect;

	/// <summary>
	/// Height of the screen when the scaling style is set to FixedSize.
	/// </summary>

	public int manualHeight = 720;

	/// <summary>
	/// If the screen height goes below this value, it will be as if the scaling style
	/// is set to FixedSize with manualHeight of this value.
	/// </summary>

	public int minimumHeight = 320;

	/// <summary>
	/// If the screen height goes above this value, it will be as if the scaling style
	/// is set to FixedSize with manualHeight of this value.
	/// </summary>

	public int maximumHeight = 1536;

	/// <summary>
	/// Whether the final value will be adjusted by the device's DPI setting.
	/// </summary>

	public bool adjustByDPI = false;

	/// <summary>
	/// If set and the game is in portrait mode, the UI will shrink based on the screen's width instead of height.
	/// </summary>

	public bool shrinkPortraitUI = false;

	/// <summary>
	/// UI Root's active height, based on the size of the screen.
	/// </summary>
    public int manualWidth = 800;

	public int activeHeight
	{
		get
        { 
            int h = Screen.height;
            int w = Screen.width;
            int height = Mathf.Max(2, h);
			if (scalingStyle == Scaling.FixedSize) return manualHeight;
            if (scalingStyle == Scaling.FixedSizeOnMobiles)
                return manualHeight;
            if (height < minimumHeight) height = minimumHeight;
			if (height > maximumHeight) height = maximumHeight;
			if (shrinkPortraitUI && h > w) height = Mathf.RoundToInt(height * ((float)h / w));
			// Adjust the final value by the DPI setting
			return adjustByDPI ? NGUIMath.AdjustByDPI(height) : height; 
        }
	}

	/// <summary>
	/// Pixel size adjustment. Most of the time it's at 1, unless the scaling style is set to FixedSize.
	/// </summary>

	public float pixelSizeAdjustment { get { return GetPixelSizeAdjustment(Screen.height); } }

	/// <summary>
	/// Helper function that figures out the pixel size adjustment for the specified game object.
	/// </summary>

	static public float GetPixelSizeAdjustment (GameObject go)
	{
		UIRoot root = NGUITools.FindInParents<UIRoot>(go);
		return (root != null) ? root.pixelSizeAdjustment : 1f;
	}

	/// <summary>
	/// Calculate the pixel size adjustment at the specified screen height value.
	/// </summary>

	public float GetPixelSizeAdjustment (int height)
	{
		height = Mathf.Max(2, height);

		if (scalingStyle == Scaling.FixedSize)
			return (float)manualHeight / height;

#if UNITY_IPHONE || UNITY_ANDROID
		if (scalingStyle == Scaling.FixedSizeOnMobiles)
			return (float)manualHeight / height;
#endif
		if (height < minimumHeight) return (float)minimumHeight / height;
		if (height > maximumHeight) return (float)maximumHeight / height;
		return 1f;
	}

	Transform mTrans;

	protected virtual void Awake () { mTrans = transform; }
	protected virtual void OnEnable () { list.Add(this); }
	protected virtual void OnDisable () { list.Remove(this); }

	protected virtual void Start ()
	{
        UIOrthoCamera oc = GetComponentInChildren<UIOrthoCamera>();
		if (oc != null)
		{
			Debug.LogWarning("UIRoot should not be active at the same time as UIOrthoCamera. Disabling UIOrthoCamera.", oc);
			Camera cam = oc.gameObject.GetComponent<Camera>();
			oc.enabled = false;
			if (cam != null) cam.orthographicSize = 1f;
		}
		else Update();
	}

	void Update ()
	{
#if UNITY_EDITOR
		if (!Application.isPlaying && gameObject.layer != 0)
			UnityEditor.EditorPrefs.SetInt("NGUI Layer", gameObject.layer);
#endif
		if (mTrans != null)
		{
            if (scalingStyle == Scaling.FixedSizeOnMobiles)
            {
                float aspect = System.Convert.ToSingle(Screen.width) / System.Convert.ToSingle(Screen.height);
                float size_x = 2.0f * aspect / System.Convert.ToSingle(manualWidth);
                float size_y = 2.0f / System.Convert.ToSingle(manualHeight);
                if (Mathf.Abs(mTrans.localScale.x - size_x) > float.Epsilon ||
                   Mathf.Abs(mTrans.localScale.y - size_y) > float.Epsilon)
                {
                    mTrans.localScale = new Vector3(size_x,size_y,1);
                }
            }
            else
            {
                float size = 2.0f / System.Convert.ToSingle(activeHeight);
                Vector3 ls = mTrans.localScale;
                if (!(Mathf.Abs(ls.x - size) <= float.Epsilon) ||
                    !(Mathf.Abs(ls.y - size) <= float.Epsilon) ||
                    !(Mathf.Abs(ls.z - size) <= float.Epsilon)){
                    mTrans.localScale = new Vector3(size, size, size);
                }
            }
		}
	}

	/// <summary>
	/// Broadcast the specified message to the entire UI.
	/// </summary>

	static public void Broadcast (string funcName)
	{
#if UNITY_EDITOR
		if (Application.isPlaying)
#endif
		{
			for (int i = 0, imax = list.Count; i < imax; ++i)
			{
				UIRoot root = list[i];
				if (root != null) root.BroadcastMessage(funcName, SendMessageOptions.DontRequireReceiver);
			}
		}
	}

	/// <summary>
	/// Broadcast the specified message to the entire UI.
	/// </summary>

	static public void Broadcast (string funcName, object param)
	{
		if (param == null)
		{
			// More on this: http://answers.unity3d.com/questions/55194/suggested-workaround-for-sendmessage-bug.html
			Debug.LogError("SendMessage is bugged when you try to pass 'null' in the parameter field. It behaves as if no parameter was specified.");
		}
		else
		{
			for (int i = 0, imax = list.Count; i < imax; ++i)
			{
				UIRoot root = list[i];
				if (root != null) root.BroadcastMessage(funcName, param, SendMessageOptions.DontRequireReceiver);
			}
		}
	}
}
