using UnityEngine;
using System.Collections;
using System.IO;
using ZXing;
using ZXing.QrCode;

public class ZXTwoCodeEvent : MonoBehaviour
{
   
    public DecoderEvent decoder;

    GameObject root;

	// Use this for initialization
	void Start () {
        root = gameObject.transform.parent.parent.parent.gameObject;
	}
	
	// Update is called once per frame
	void Update () {

	}

    public void logic()
    {
        if (decoder == null)
        {
            GameObject obj = Resources.Load<GameObject>("prefabs/decoder");
            GameObject decoderObj = NGUITools.AddChild(root,obj);
            decoderObj.name = "decoder";
            decoder = decoderObj.GetComponent<DecoderEvent>();
            decoder.CallBack = new EventDelegate(over);
            StartCoroutine(decoder.open());
        }
    }

    public void over()
    {
        for (int i = 0; i < root.transform.childCount; i++ )
        {
            GameObject obj = root.transform.GetChild(i).gameObject;
            if (obj.name.Equals("decoder"))
            {
                obj.SetActive(false);
                Destroy(obj,1);
                break;
            }
        }
        decoder = null;
    }
}
 
