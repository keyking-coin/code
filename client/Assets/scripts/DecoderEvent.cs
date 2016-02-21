using UnityEngine;
using System.Collections;
using ZXing;
using ZXing.QrCode;

public class DecoderEvent : MonoBehaviour {

    WebCamTexture cameraTexture = null;

    UITexture showTexture;

    BarcodeReader reader = null;

    int count = 0;

    int countFps = 0;

    const int FPS = 24;

    private EventDelegate callBack;

    GameObject line;

    GameObject main;

    float speed = -0.005f;

    Color32[] temps = null;

    Texture2D t2d = null;

	//Use this for initialization
	void Start () {
        showTexture = gameObject.GetComponentInChildren<UITexture>();
        reader = new BarcodeReader { AutoRotate = true, TryHarder = true };
        line = gameObject.transform.FindChild("line").gameObject;
        main = GameObject.Find("main");
	}

	private void _transform(){
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

	// Update is called once per frame
	void Update () {
        if (cameraTexture != null)
        {
            countFps ++;
            if (countFps == FPS)
            {
                _transform();
                countFps = 0;
            }
            count++;
            if (count == 100)
            {
                scan();
                count = 0;
            }
            if (line.transform.localPosition.y >= showTexture.height / 2 - 5 || line.transform.localPosition.y <= 5 - showTexture.height / 2)
            {
                speed = -speed;
            }
            line.transform.Translate(0,speed,0);
        }
	}

    private void scan(){
        Result result = reader.Decode(temps,cameraTexture.height,cameraTexture.width);
        if (result != null)
        {
            DialogUtil.tip(result.Text);
            UIPlaySound sound = gameObject.GetComponent<UIPlaySound>();
            sound.Play();
            cameraTexture.Stop();
            if (callBack != null)
            {
                callBack.Execute();
            }
        }
    }

    public IEnumerator open()
    {
        yield return Application.RequestUserAuthorization(UserAuthorization.WebCam);
        if (Application.HasUserAuthorization(UserAuthorization.WebCam))
        {
            WebCamDevice[] devices = WebCamTexture.devices;
            string deviceName = devices[0].name;
            cameraTexture = new WebCamTexture(deviceName,256,256,FPS);
            cameraTexture.Play();
            int w = cameraTexture.height;
            int h = cameraTexture.width;
            float scale = main.transform.localScale.x / main.transform.localScale.y;
            showTexture.width = (int)(h / scale);
            showTexture.height = h;
            UISprite sprite = line.GetComponent<UISprite>();
            sprite.width = showTexture.width;
            temps = new Color32[w*h];
            t2d = new Texture2D(w,h);
            showTexture.mainTexture = t2d;
            _transform();
        }
    }

    public EventDelegate CallBack
    {
        set
        {
            callBack = value;
        }
    }
}
 
