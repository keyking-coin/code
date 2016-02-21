using UnityEngine;
using System.Collections;
using System.IO;

public class PicTools : MonoBehaviour {
	
	public Camera _camera;

	public UILabel lx,ly,lw,lh;

	//Use this for initialization
	void Start () {

	}

	// Update is called once per frame
	void Update () {
	
	}

	public void click(){
		StartCoroutine (CaptureScreenshot2());
	}

	IEnumerator CaptureScreenshot2() { 
		RenderTexture rt = new RenderTexture (940,1028,0);
        _camera.targetTexture = rt;
        _camera.Render();
		RenderTexture.active = rt;

		float startx = 104, starty = 312 , lenx = 50 , leny = 52;
		int count = 1;
		float[] offxs = new float[]{0,-1.0f,-1.0f,-1.0f,-3.0f,-4.0f,-4.0f,-6.0f,-6.0f,-6.0f,-7.0f,-7.0f,-9.0f,-10.0f,-11.0f,-5.0f};
		float[] offys = new float[]{0,-0.5f,-1.0f,-3.0f,-5.0f,-6.0f,-8.0f,-9.5f};
		for (int j = 0 ; j < 8 ; j++){
			for (int i = 0 ; i < 15 ; i++){
				float x = startx + i * lenx + offxs[i];
				float y = starty + j * leny + offys[j];
				float w = 40.0f;
				if (i == 14){
					w = 36.0f;
				}
				Rect rect = new Rect(x,y,w,40);
				Texture2D screenShot = new Texture2D((int)rect.width,(int)rect.height,TextureFormat.RGB24,false);
				screenShot.ReadPixels(rect,0,0,true);
				screenShot.Apply();
                File.WriteAllBytes("E:/temp/e" + count + ".jpg", screenShot.EncodeToJPG());
				count ++;
			}
		}
	    /*
		Rect rect = new Rect(float.Parse(lx.text),float.Parse(ly.text),float.Parse(lw.text),float.Parse(lh.text));
		Texture2D screenShot = new Texture2D((int)rect.width,(int)rect.height,TextureFormat.RGB24,false);
		screenShot.ReadPixels(rect,0,0,true);
		screenShot.Apply();
		UITexture u_texture = gameObject.GetComponentsInChildren<UITexture>()[0];
		u_texture.mainTexture = screenShot;
		u_texture.width  = (int)rect.width;
		u_texture.height = (int)rect.height;
		 */
        _camera.targetTexture = null;
		RenderTexture.active = null;
		Destroy (rt);
		yield return null;
	}

    public void test()
    {
        RenderTexture rt = new RenderTexture(940, 1028, 0);
        _camera.targetTexture = rt;
        _camera.Render();
        RenderTexture.active = rt;
        Rect rect = new Rect(0,0,128,256);
        Texture2D screenShot = new Texture2D((int)rect.width, (int)rect.height, TextureFormat.RGB24, false);
        screenShot.ReadPixels(rect, 0, 0, true);
        screenShot.Apply();
        Color32[] datas = screenShot.GetPixels32();
        Debug.Log("datas : " + datas.Length);
        RenderTexture.active = null;
        _camera.targetTexture = null;
    }
}
 
 
