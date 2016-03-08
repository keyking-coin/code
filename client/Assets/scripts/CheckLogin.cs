using UnityEngine;
using System.Collections;

public class CheckLogin : MonoBehaviour
{
    GameObject login_obj = null;

    GameObject main_obj = null;

	// Use this for initialization
	void Start () {
        main_obj  = transform.parent.parent.parent.gameObject;
        login_obj = transform.parent.parent.parent.parent.FindChild("frame-login").gameObject;

	}
	
	// Update is called once per frame
	void Update () {

	}

    void _openLogin()
    {
        ConfirmUtil.TryToDispear();
        main_obj.SetActive(false);
        login_obj.SetActive(true);
    }

    public void openLogin()
    {
        if (MainData.instance.user.login())
        {
            ConfirmUtil.confirm("切换账号？", _openLogin);
        }
        else
        {
            _openLogin();
        }
    }
}
