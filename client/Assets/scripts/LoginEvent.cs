using UnityEngine;
using System.Collections;

public class LoginEvent : MonoBehaviour {

    UIInput account , pwd;

    static GameObject login_obj ,  main_obj;

    public static EventDelegate callback = null;
    
	// Use this for initialization
	void Start () {
        account = transform.FindChild("account").GetComponent<UIInput>();
        pwd     = transform.FindChild("pwd").GetComponent<UIInput>();
        checkInit();
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("Login");
        if (buffer != null)
        {//登录成功
            MainData.instance.user.deserialize(buffer);
            MainData.instance.deserializeSimpleOrderModule(buffer);
            enterIn();
            if (callback != null)
            {
                callback.Execute();
            }
		}
        buffer = MyUtilTools.tryToLogic("Tourist");
        if (buffer != null)
        {//游客访问
            MainData.instance.deserializeSimpleOrderModule(buffer);
            enterIn();
        }
	}

	public void login(){
        if (MyUtilTools.stringIsNull(account.value))
        {
			DialogUtil.tip("请输入您的账号 ");
			return;
		}
        if (MyUtilTools.stringIsNull(pwd.value))
        {
            DialogUtil.tip("请输入您的密码 ");
			return;
		}
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("Login");
        buffer.WriteString(account.value);
        buffer.WriteString(pwd.value);
        NetUtil.getInstance.SendMessage (buffer);
	}

    private void enterIn()
    {
        login_obj.SetActive(false);
        main_obj.SetActive(true);
    }

    public void access()
    {

        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("Tourist");
        NetUtil.getInstance.SendMessage (buffer);
        /*
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        AndroidJavaObject ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        ajo.Call("testNotification");*/
    }

    public void goToNext(GameObject to)
    {
        to.SetActive(true);
        gameObject.SetActive(false);
    }

    public static void openLogin()
    {
        ConfirmUtil.TryToDispear();
        checkInit();
        login_obj.SetActive(true);
        main_obj.SetActive(false);
    }

    public static void tryToLogin()
    {
        ConfirmUtil.confirm("未登录,去登录？",openLogin);
        callback = null;
    }

    static void checkInit()
    {
        if (login_obj == null || main_obj == null)
        {
            GameObject main = GameObject.Find("main");
            login_obj = main.transform.FindChild("frame-login").gameObject;
            main_obj = main.transform.FindChild("frame-main").gameObject;
        }
    }
}
 
 
