using UnityEngine;
using System.Collections;

public class FindPasswordEvent : MonoBehaviour {

    UIInput num_input;

    long startTime = 0;

    UILabel labe;

    UIButton button;

    GameObject tips_obj;

	// Use this for initialization
	void Start () {
        num_input = gameObject.GetComponentInChildren<UIInput>();
        Transform next = gameObject.transform.FindChild("next");
        labe   = next.GetComponentInChildren<UILabel>();
        button = next.GetComponent<UIButton>();
        tips_obj = gameObject.transform.FindChild("tips").gameObject;
    }
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("FindPassword");
        if (buffer != null)
        {
            tips_obj.SetActive(true);
            startTime = System.DateTime.Now.Ticks / 10000000;
            labe.color = Color.red;
            button.SetState(UIButtonColor.State.Disabled,true);
        }
        if (startTime > 0)
        {
            long now = System.DateTime.Now.Ticks / 10000000;
            long fix = now - startTime;
            if (fix >= 60)
            {
                init();
            }
            else
            {
                labe.text = "剩余" + (60 - fix) + " 秒";
            }
        }
	}

    public void next()
    {
        if (num_input.value != null && startTime == 0)
        {
            if (MyUtilTools.stringIsNull(num_input.value))
            {
                DialogUtil.tip("请输入手机号码");
                return;
            }
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("FindPassword");
            buffer.WriteString(num_input.value);
            NetUtil.getInstance.SendMessage(buffer);
            tips_obj.SetActive(false);
        }
    }

    public void goToNext(GameObject to)
    {
        to.SetActive(true);
        gameObject.SetActive(false);
        init();
        tips_obj.SetActive(false);
    }

    public void init()
    {
        labe.text = "下一步";
        labe.color = Color.black;
        button.SetState(UIButtonColor.State.Normal,true);
        startTime = 0;
    }
}
