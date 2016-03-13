using UnityEngine;
using System.Collections;

public class AdminLogin : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminLogin");
        if (buffer != null)
        {
            transform.parent.gameObject.SetActive(false);
            transform.parent.parent.FindChild("admin-main").gameObject.SetActive(true);
        }
	}

    public void login()
    {
        UIInput account = transform.FindChild("account").GetComponent<UIInput>();
        UIInput pwd     = transform.FindChild("pwd").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(account.value))
        {
            DialogUtil.tip(account.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        if (MyUtilTools.stringIsNull(pwd.value))
        {
            DialogUtil.tip(pwd.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminLogin");
        buffer.WriteString(account.value);
        buffer.WriteString(pwd.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void goTofind()
    {
        transform.parent.FindChild("find").gameObject.SetActive(true);
        transform.gameObject.SetActive(false);
    }
}
