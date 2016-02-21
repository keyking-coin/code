using UnityEngine;
using System.Collections;
using System;
using System.IO;
using UnityEngine.UI;
using System.Net;
using System.Net.Sockets;
using System.Runtime.InteropServices;

public class NetTest : MonoBehaviour {

	public Text show;

	// Use this for initialization
	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
		if (show != null){
			ByteBuffer buffer = NetUtil.getInstance.find("Token");
			if (buffer != null){
				int result = buffer.ReadInt();
				if (result == 0){
					string code = buffer.ReadString();
					show.text = "通信成功" + code;
				}else{
					show.text = "通信失败";
				}
				NetUtil.getInstance.remove("Token");
			}
		}
	}

	private IEnumerator openGetURL(String str){
		Debug.Log("尝试连接" + str);
		WWW www = new WWW (str);
		yield return www;
		if (www.error != null){
			show.text = www.error;
			yield return null;
		}
		show.text = www.text;
	}

	private IEnumerator openHttpPostURL(){
		string url = "http://127.0.0.1:8080/BearFarm/Bear";
		Debug.Log("尝试连接" + url);
		WWWForm form = new WWWForm ();
		form.AddField ("logic","Update");
		form.AddField ("setUid,long","1");
		form.AddField ("setName,String","Tom");
		//form.AddBinaryData ("setMemory",System.Text.UTF8Encoding.UTF8.GetBytes("abcdesadadadada"),"test.txt","application/octet-stream");
		form.AddBinaryData ("null",new byte[]{1},"","application/octet-stream");
		WWW www = new WWW (url,form);
		yield return www;
		if (www.error != null){
			show.text = www.error;
			yield return null;
		}
		show.text = www.text;
	}

	public void openHttp(){
		StartCoroutine (openHttpPostURL());
	}

    public void openSocket(){
		//ByteBuffer buffer = ByteBuffer.Allocate(1024);
		//buffer.skip(4);
		//buffer.WriteString("Token");
		//buffer.WriteString("13856094894");
		//buffer.WriteInt(0,buffer.getWriteIndex()-4);
		//byte[] arrays = buffer.ToArray();
		//NetUtil.getInstance.SendMessage(arrays);
    }

	public void OnGUI(){

	}
}
 
 
