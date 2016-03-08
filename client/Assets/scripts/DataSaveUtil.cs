using UnityEngine;
using System.Collections;
using LitJson;

public class DataSaveUtil : MonoBehaviour {

    GameObject login_obj;

    GameObject main_obj;

	// Use this for initialization
	void Start () {
        login_obj = transform.FindChild("frame-login").gameObject;
        main_obj = transform.FindChild("frame-main").gameObject;
#if UNITY_EDITOR
        string path = "file:///" + Application.persistentDataPath + "/login_save.data";
#elif UNITY_IPHONE
		string path = Application.persistentDataPath + "/login_save.data";
#else
		string path = "file://" + Application.persistentDataPath + "/login_save.data";
#endif
        StartCoroutine(load(path));
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AutoLogin");
        if (buffer != null)
        {//登录成功
            MainData.instance.user.deserialize(buffer);
            MainData.instance.deserializeSimpleOrderModule(buffer);
            login_obj.SetActive(false);
            main_obj.SetActive(true);
        }
	}

    public void save(string account , string pwd)
    {
        string str = "{\"account\":\"" + account + "\",\"pwd\":\"" + pwd + "\"}";
        //byte[] datas = System.Convert.FromBase64String(str);
        byte[] datas = System.Text.Encoding.UTF8.GetBytes(str);
        JustRun.Instance.tryToSavePicToLocal(JustRun.SaveBodyEntity.create("login_save.data",datas));
    }

    private IEnumerator load(string path)
    {
        WWW www = new WWW(path);
        Debug.Log("load pic from " + path);
        yield return www;
        if (www.isDone)
        {
            if (www.error == null)
            {
                byte[] datas = www.bytes;
                //string str = System.Convert.ToBase64String(datas);
                string str = System.Text.Encoding.UTF8.GetString(datas);
                JsonData jd = JsonMapper.ToObject(str);
                JsonData content = jd["account"];
                string account = content.ToString();
                content = jd["pwd"];
                string pwd = content.ToString();
                ByteBuffer buffer = ByteBuffer.Allocate(1024);
                buffer.skip(4);
                buffer.WriteString("AutoLogin");
                buffer.WriteString(account);
                buffer.WriteString(pwd);
                NetUtil.getInstance.SendMessage(buffer);
            }
        }
    }
}
