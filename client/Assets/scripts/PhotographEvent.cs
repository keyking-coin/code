using UnityEngine;
using System.Collections;
using System.IO;
using ZXing;
using ZXing.QrCode;

public class PhotographEvent : MonoBehaviour
{
    //WebCamTexture cameraTexture = null;

    //int FPS = 20;

    //Color32[] temps = null;

    //Texture2D t2d = null;

    //int countFps = 0;

    UIPanel panel = null;

    EventDelegate callBack;

	void Start (){

	}
	
	void Update () {

	}
/*
    public void open(UITexture target, UIPanel panel)
    {
        gameObject.SetActive(true);
        this.panel = panel;
        panel.alpha = 0.1f;
        CameraUtil.push(4,3);
#if !UNITY_EDITOR
        StartCoroutine(openCamera());
#endif
    }
*/
    public void open(EventDelegate over)
    {
        gameObject.SetActive(true);
        if (panel == null)
        {
            panel = GameObject.Find("main").GetComponent<UIPanel>();
        }
        panel.alpha = 0.1f;
        CameraUtil.push(4,3);
        callBack = over;
    }

    public void clear(UITexture target)
    {
		if (target != null)
		{
			target.mainTexture = null;
		}
    }
/*
    IEnumerator openCamera()
    {
        yield return Application.RequestUserAuthorization(UserAuthorization.WebCam);
        if (cameraTexture != null)
        {
            cameraTexture.Play();
        }
        else if (Application.HasUserAuthorization(UserAuthorization.WebCam))
        {
            WebCamDevice[] devices = WebCamTexture.devices;
            string deviceName = devices[0].name;
            UITexture texture = transform.FindChild("rect").FindChild("Texture").GetComponent<UITexture>();
            cameraTexture = new WebCamTexture(deviceName,texture.width,texture.height,FPS);
            cameraTexture.Play();
            int w = cameraTexture.height;
            int h = cameraTexture.width;
            temps = new Color32[w*h];
            t2d = new Texture2D(w,h);
            _transform(); 
            texture.mainTexture = t2d;
        }
    }

    private void _transform()
    {
        Color32[] datas = cameraTexture.GetPixels32();
        int w = cameraTexture.width;
        int h = cameraTexture.height;
        for (int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                int index1 = i * w + j;
                int index2 = (w - j - 1) * h + i;
                temps[index2] = datas[index1];
            }
        }
        t2d.SetPixels32(temps);
        t2d.Apply();
    }

    public void picture()
    {
        if (cameraTexture != null)
        {
            _transform();
            cameraTexture.Stop();
            Texture2D texture2d = new Texture2D(t2d.width,t2d.height);
            texture2d.LoadImage(t2d.EncodeToPNG());
            //target.mainTexture = texture2d;
            if (callBack != null)
            {
                callBack.parameters[0] = new EventDelegate.Parameter();
                callBack.parameters[0].obj = texture2d;
                callBack.Execute();
            }
        }
        over();
    }
 */
    public void over()
    {
        CameraUtil.pop(4);
        panel.alpha = 1f;
        gameObject.SetActive(false);
        callBack = null;
    }

    void backFromTelephone(string url)
    {
        StartCoroutine(_loadPic(url));
    }

    private IEnumerator _loadPic(string name)
    {
        string fileName = "file://" + name;
        Debug.Log("load pic " + fileName);
        WWW www = new WWW(fileName);
        yield return www;
        if (www.isDone)
        {
            if (www.error == null)
            {
                if (callBack != null)
                {
                    callBack.parameters[0] = new EventDelegate.Parameter();
                    callBack.parameters[0].obj = www.texture;
                    callBack.Execute();
                    over();
                }
            }
        }
    }

    public void picture()
    {
        #if UNITY_ANDROID
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("picture",Application.persistentDataPath);
        #endif
    }

    public void select()
    {
        #if UNITY_ANDROID
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("select", Application.persistentDataPath);
        #endif
    }
}
 
