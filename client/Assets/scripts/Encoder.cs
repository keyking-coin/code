using UnityEngine;
using System.Collections;
using ZXing;
using ZXing.QrCode;

public class Encoder : MonoBehaviour {

	private Texture2D encoded;

	bool show = false;

	// Use this for initialization
	void Start () {
		encoded = new Texture2D(256,256);
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	private static Color32[] Encode(string textForEncoding, int width, int height){
		var writer = new BarcodeWriter{
			Format = BarcodeFormat.QR_CODE,
			Options = new QrCodeEncodingOptions{
				Height = height,
				Width = width
			}
		};
		return writer.Write(textForEncoding);
	}

	public void open(){
        if(Application.isMobilePlatform){
            return;
        }
		show = !show;
        Color32[] color32 = Encode("Hello World", encoded.width, encoded.height);
		encoded.SetPixels32(color32);
		encoded.Apply();
	}
	
	void OnGUI(){
		if (show) {
			GUI.DrawTexture(new Rect(200,50,256,256),encoded);
		}
	}
}
 
 
