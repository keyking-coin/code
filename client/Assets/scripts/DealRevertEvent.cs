using UnityEngine;
using System.Collections;

public class DealRevertEvent : JustChangeLayer
{
    public EventDelegate callback = null;

    DealBody curItem;

    public EventDelegate okback = null;

	void Start () {

	}
	
	// Update is called once per frame
	void Update () {
        run();
        ByteBuffer buffer = MyUtilTools.tryToLogic("RevertAdd");
        if (buffer != null)
        {
            //MainData.instance.deserializeDealModule(buffer);
            gameObject.SetActive(false);
            if (okback != null)
            {
                okback.Execute();
                okback = null;
            }
        }
	}

    public void sure()
    {
        UIInput input = gameObject.GetComponentInChildren<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip("请输入要回复的内容");
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("RevertAdd");
        buffer.WriteLong(curItem.id);
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteString(input.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void cancle()
    {
        gameObject.SetActive(false);
        if (callback != null)
        {
            callback.Execute();
        }
    }

    public void show(DealBody item)
    {
        //change(1,10,gameObject);
        gameObject.SetActive(true);
        curItem = item;
    }
}
