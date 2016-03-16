using UnityEngine;
using System;
using System.Threading;
using System.IO;
using System.Collections;
using System.Collections.Generic;


public class JustRun : MonoBehaviour {
    public static byte NULL_FLAG   = 3;
   	public static byte ADD_FLAG    = 0;
	public static byte DEL_FLAG    = 1;
	public static byte UPDATE_FLAG = 2;
    public static byte MODULE_CODE_USER         = 0;//用户数据部分
    public static byte MODULE_CODE_DEAL         = 1;//交易帖子
    public static byte MODULE_CODE_REVERT       = 2;//交易帖子回复
    public static byte MODULE_CODE_SIMPLE_DEAL  = 3;//推送数据
    public static byte MODULE_CODE_CREDIT       = 4;//信用额度
	public static byte MODULE_CODE_BANK_ACCOUNT = 5;//银行卡
    public static byte MODULE_CODE_EAMIL        = 6;//邮件
    public static byte MODULE_CODE_SIMPLE_ORDER = 7;//最进成交模块
    public static byte MODULE_CODE_FRIEND       = 8;//好友
    public static byte MODULE_CODE_MESSAGE      = 9;//聊天
	public static byte MODULE_CODE_ORDER        = 10;//成交订单

    public static string PIC_PHP_URL = "http://www.sh-yxwlkj.com:321";

	// Use this for initialization
    public delegate void ModuleCallBack(ByteBuffer buffer);

	private static JustRun instance = null;

    Dictionary<byte,ModuleCallBack> moduleCalls = new Dictionary<byte,ModuleCallBack>();

	Dictionary<string,Texture> pic_caches = new Dictionary<string, Texture>();

	PushEvent pushEvent;

	public class SaveBodyEntity
	{
		string path;
		string name;
		byte[] datas;

        public static SaveBodyEntity create(string name,byte[] datas)
		{
			SaveBodyEntity entity = new SaveBodyEntity ();
			entity.path    = Application.persistentDataPath;
			entity.name    = name;
            entity.datas   = datas;
			return entity;
		}

        public byte[] Datas
        {
            get
            {
                return datas;
            }
        }

        public string savepath
        {
            get
            {
                string str = null;
                int index = name.LastIndexOf("/");
                if (index > 0)
                {
                    string directory_path = path + "/" + name.Substring(0,index);
                    if (!Directory.Exists(directory_path))
                    {
                        Directory.CreateDirectory(directory_path);
                    }
                    str = directory_path + "/" + name.Substring(index+1,name.Length-index-1);
                }
                else
                {
                    str = path + "/" + name;
                }
                return str;
            }
        }
	}

	void Start () {
		instance = this;
        put(MODULE_CODE_DEAL,MainData.instance.deserializeDealModuleOne);
        put(MODULE_CODE_BANK_ACCOUNT, MainData.instance.user.bacnkAccount.deserializeModule);
		put(MODULE_CODE_SIMPLE_DEAL,readPushData);
        put(MODULE_CODE_EAMIL,MainData.instance.deserializeEmailModule);
        put(MODULE_CODE_SIMPLE_ORDER,MainData.instance.deserializeSimpleOrderModuleOne);
        put(MODULE_CODE_FRIEND,MainData.instance.deserializeFriendModuleOne);
        put(MODULE_CODE_MESSAGE,MainData.instance.deserializeMessageModuleOne);
        put(MODULE_CODE_ORDER, MainData.instance.deserializeOrderModuleOne);
        GameObject push_obj = GameObject.Find("push") ;
        if (push_obj != null)
        {
            pushEvent = push_obj.GetComponent<PushEvent>();
        }
	}
    public static JustRun Instance
    {
        get
        {
            if (instance == null)
            {
                instance = GameObject.Find("Camera").GetComponent<JustRun>();
            }
            return instance;
        }
    }

	void goToLogin(){
		LoginEvent.openLogin();
	}

	// Update is called once per frame
	void Update () {
		if (NetUtil.getInstance.mustLogin && !ConfirmUtil.isConfirmShow() && !LoadUtil.isActivity() && !DialogUtil.isPopTips())
		{
			pushEvent.ignoreAll();
			NetUtil.getInstance.mustLogin = false;
			ConfirmUtil.confirm("你的账号在别处登录了",goToLogin,exit);
			return;
		}
        List<ByteBuffer> modules = NetUtil.getInstance.Module;
        if (modules.Count > 0)
        {
            foreach (ByteBuffer buffer in modules)
            {
                buffer.ReadInt();//成功
                int size = buffer.ReadInt();
                for (int i = 0; i < size; i++ )
                {
                    byte code = buffer.ReadByte();
					if (moduleCalls.ContainsKey(code))
					{
						ModuleCallBack call = moduleCalls[code];
						call(buffer);
					}
                }
            }
            modules.Clear();
        }
		modules = NetUtil.getInstance.Cachs;
		if (modules.Count > 0)
		{
			modules.Clear();
		}
        if (Input.GetKeyDown(KeyCode.Escape))
        {
            if (LoadUtil.isActivity())
            {
                return;
            }
            if (ConfirmUtil.isConfirmShow())
            {
                ConfirmUtil.TryToDispear();
                return;
            }
            GameObject photographer = GameObject.Find("photographer");
            if (photographer != null && photographer.activeSelf)
            {
                photographer.GetComponent<PhotographEvent>().over();
                return;
            }
            GameObject popup_select = GameObject.Find("popup-select");
            if (popup_select != null && popup_select.activeSelf)
            {
                popup_select.GetComponent<PopupListEvent>().close();
                return;
            }
            if (DialogUtil.isPopTips()){
                DialogUtil.dispear();
                return;
            }
            ConfirmUtil.confirm("是否退出?",exit);
        }
	}

	void readPushData(ByteBuffer buffer)
	{
		byte flag = buffer.ReadByte();
		if (flag == JustRun.ADD_FLAG)
		{
			DealBody item = DealBody.read(buffer);
			PushEvent.pushs.Add(item);
		}
	}

    public void put(byte code , ModuleCallBack call)
    {
		if (!moduleCalls.ContainsKey(code))
        {
			moduleCalls.Add(code,call);
        }
    } 

    public void OnApplicationQuit()
    {
        NetUtil.getInstance.Closed();
    }

    public void exit()
    {
        ConfirmUtil.TryToDispear();
        Application.Quit();
    }

    public void upLoadPic(string name, byte[] datas, EventDelegate ok = null,EventDelegate fail = null)
    {
        string url = PIC_PHP_URL + "/sendfile.php";
        StartCoroutine(uploadPicToNet(url,name,datas,ok,fail));
    }

    private IEnumerator uploadPicToNet(string url, string name, byte[] datas, EventDelegate ok, EventDelegate fail)
    {
        WWWForm form = new WWWForm();
        form.AddBinaryData("file",datas,name,"multipart/form-data");
        WWW www = new WWW(url,form);
        Debug.Log("upload pic to " + url);
        yield return www;
        if (www.isDone)
        {
            if (www.error != null)
            {
                if (fail != null)
                {
                    fail.Execute();
                }
            }
            else
            {
                if (ok != null)
                {
                    ok.Execute();
                }
            }
        }
    }

    public void tryToSavePicToLocal(SaveBodyEntity entity)
    {
        Debug.Log("try to save data to local ---> " + entity.savepath);
        Thread thread = new Thread(new ParameterizedThreadStart(saveToFile));
        thread.IsBackground = true;
        thread.Start(entity);
    }

	public void loadPic(string name , UITexture texture , Texture src = null)
	{
        if (pic_caches.ContainsKey(name))
		{
            texture.mainTexture = pic_caches[name];
			return;
		}
        if (src == null)
        {
            StartCoroutine(_loadPic(name, texture));
        }
        else
        {
            texture.mainTexture = src;
            pic_caches.Add(name,src);
            tryToSavePicToLocal(SaveBodyEntity.create(name, ((Texture2D)src).EncodeToJPG()));
        }
	}

    private IEnumerator _loadPic(string name, UITexture texture)
	{
#if UNITY_EDITOR
		string path = "file:///" + Application.persistentDataPath + "/" + name;
#elif UNITY_IPHONE
		string path = Application.persistentDataPath + "/" + name;
#else
		string path = "file://" + Application.persistentDataPath + "/" + name;
#endif
		WWW www = new WWW(path);
		Debug.Log ("load pic from " + path);
		yield return www;
		if (www.isDone){
			if (www.error == null)
			{
				texture.mainTexture = www.texture;
                if (!pic_caches.ContainsKey(name))
                {
                    pic_caches.Add(name, www.texture);
                }
			}
			else//不存在文件
			{
				Debug.Log (name + " not exist try to load from net");
                StartCoroutine(loadPicFromNet(name, texture));
			}
		}
	}

    private IEnumerator loadPicFromNet(string name, UITexture texture)
    {
        string path = PIC_PHP_URL + "/uploads/" + name;
        WWW www = new WWW(path);
        yield return www;
        if (www.isDone)
        {
            if (www.error == null)
            {
                texture.mainTexture = www.texture;
                pic_caches.Add(name,www.texture);
                tryToSavePicToLocal(SaveBodyEntity.create(name,www.texture.EncodeToJPG()));
            }
            else
            {//加载失败,继续加载
                StartCoroutine(loadPicFromNet(name, texture));
            }
        }
    }

	void saveToFile(object obj)
	{
		SaveBodyEntity entity = (SaveBodyEntity)obj;
        FileInfo file = new FileInfo(entity.savepath);
		FileStream stream = null;
		if (!file.Exists)
		{
			stream = file.Create ();
		}
		else 
		{
			stream = file.OpenWrite();
		}
        stream.Write(entity.Datas,0,entity.Datas.Length);
		stream.Close ();
		stream.Dispose ();
	}
}
 
