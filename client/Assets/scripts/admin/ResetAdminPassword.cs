using UnityEngine;
using System.Collections;

public class ResetAdminPassword : MonoBehaviour {

	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminResetPwd");
        if (buffer != null)
        {
            DialogUtil.tip("密码重置成功",true);
        }
	}

    public void reset()
    {
        UIInput account  = transform.FindChild("account").GetComponent<UIInput>();
        UIInput safecode = transform.FindChild("code").GetComponent<UIInput>();
        UIInput pwd      = transform.FindChild("pwd").GetComponent<UIInput>();
        UIInput rpwd     = transform.FindChild("rpwd").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(account.value))
        {
            DialogUtil.tip(account.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        if (MyUtilTools.stringIsNull(safecode.value))
        {
            DialogUtil.tip(safecode.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        if (MyUtilTools.stringIsNull(pwd.value))
        {
            DialogUtil.tip(pwd.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        if (MyUtilTools.stringIsNull(rpwd.value))
        {
            DialogUtil.tip(rpwd.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        if (!pwd.value.Equals(rpwd.value))
        {
            DialogUtil.tip("两次输入的密码不匹配");
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminResetPwd");
        buffer.WriteString(account.value);
        buffer.WriteString(safecode.value);
        buffer.WriteString(pwd.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void back()
    {
        transform.parent.FindChild("login").gameObject.SetActive(true);
        transform.gameObject.SetActive(false);
    }
}
