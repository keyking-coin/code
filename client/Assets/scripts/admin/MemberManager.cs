using UnityEngine;
using System.Collections.Generic;

public class MemberManager : MonoBehaviour {

    List<MainData.UserData> users = new List<MainData.UserData>();

    int selectIndex = 0;

    GameObject pref_user_info;

	// Use this for initialization
	void Start () {
        pref_user_info = Resources.Load<GameObject>("prefabs/user-info");
	}

    void refresh()
    {
        Transform body      = transform.FindChild("left").FindChild("list").FindChild("body");
        body.localPosition = Vector3.zero;
        body.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        Transform container = body.FindChild("container");
        MyUtilTools.clearChild(container);
        float y = 800f;
        for (int i = 0; i < users.Count; i++ )
        {
            MainData.UserData user = users[i];
            GameObject user_summary = NGUITools.AddChild(container.gameObject,pref_user_info);
            user_summary.transform.localPosition = new Vector3(-180,y,0);
            user_summary.name = "" + i;
            user_summary.transform.FindChild("icon").GetComponent<UISprite>().spriteName = user.face;
            user_summary.transform.FindChild("name").GetComponent<UILabel>().text = user.nikeName;
            Transform select_trans = user_summary.transform.FindChild("select");
            select_trans.FindChild("show").gameObject.SetActive(i == selectIndex);
            UIButton button = select_trans.GetComponent<UIButton>();
            EventDelegate event_delegate = new EventDelegate(this, "select");
            event_delegate.parameters[0] = new EventDelegate.Parameter();
            event_delegate.parameters[0].obj = user_summary;
            button.onClick.Add(event_delegate);
            y -= 80;
        }
        refreshRight();
    }

    void select(GameObject obj)
    {
        int index = int.Parse(obj.name);
        if (selectIndex == index)
        {
            return;
        }
        Transform container = transform.FindChild("left").FindChild("list").FindChild("body").FindChild("container");
        Transform preTrans = container.FindChild(selectIndex+"");
        if (preTrans != null)
        {
            preTrans.FindChild("select").FindChild("show").gameObject.SetActive(false);
        }
        Transform curTrans = container.FindChild(index + "");
        if (curTrans != null)
        {
            curTrans.FindChild("select").FindChild("show").gameObject.SetActive(true);
        }
        selectIndex = index;
        refreshRight();
    }

    void refreshRight()
    {
        Transform container = transform.FindChild("right");
        if (selectIndex > users.Count)
        {
            return;
        }
        MainData.UserData user = users[selectIndex];
        container.FindChild("account").FindChild("value").GetComponent<UILabel>().text = user.account;
        container.FindChild("nickName").FindChild("inputer").GetComponent<UIInput>().value = user.nikeName;
        container.FindChild("name").FindChild("inputer").GetComponent<UIInput>().value = user.realyName;
        container.FindChild("ident").FindChild("value").GetComponent<UILabel>().text = user.indentity;

    }

	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("AdminMemberSearch");
        if (buffer != null)
        {
            int size = buffer.ReadInt();
            if (size > 0)
            {
                selectIndex = 0;
                users.Clear();
                for (int i = 0; i < size; i++)
                {
                    MainData.UserData user = new MainData.UserData();
                    user.deserialize(buffer);
                    users.Add(user);
                }
                refresh();
            }
            else
            {
                DialogUtil.tip("查找不到相关数据");
            }
        }
	}

    public void search()
    {
        UIInput input = transform.FindChild("left").FindChild("search").FindChild("inputer").GetComponent<UIInput>();
        if (MyUtilTools.stringIsNull(input.value))
        {
            DialogUtil.tip(input.transform.FindChild("tips").GetComponent<UILabel>().text);
            return;
        }
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("AdminMemberSearch");
        buffer.WriteString(input.value);
        NetUtil.getInstance.SendMessage(buffer);
    }

    public void commit()
    {

    }
}
