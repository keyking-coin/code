using UnityEngine;
using System.Collections;

public class PopupListEvent : MonoBehaviour {

    public UIPanel panel;

    UIPopupList temp;

    static GameObject pre_select;

    static GameObject pre_container;

	static GameObject pre_text_label;

    public UIDragScrollView scroll_drag;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void close()
    {
        gameObject.SetActive(false);
        panel.alpha = 1f;
        CameraUtil.pop(5);
        temp = null;
        MyUtilTools.clearChild(transform.FindChild("container"));
    }

    public void show(UIPopupList list,string title_str)
    {
        if (pre_select == null)
        {
            pre_select = Resources.Load<GameObject>("prefabs/popup-obj");
        }
        temp = list;
        panel.alpha = 0.1f;
        gameObject.SetActive(true);
        CameraUtil.push(5,3);
        if (title_str != null)
        {
            UILabel label_title = transform.FindChild("up").FindChild("title").GetComponentInChildren<UILabel>();
            label_title.text = title_str;
        }
        UITexture back = transform.FindChild("back").GetComponent<UITexture>();
        if (list.items.Count > 8){//需要滚屏
            back.height = 820;
            if (pre_container == null)
            {
                pre_container = Resources.Load<GameObject>("prefabs/popup-container");
            }
            GameObject ob_temp = NGUITools.AddChild(transform.FindChild("container").gameObject,pre_container);
            ob_temp.name = "body";
            ob_temp.transform.localPosition = new Vector3(0,-40,0);
            GameObject container = ob_temp.transform.FindChild("container").gameObject;
            float start = 270;
            for (int i = 0; i < list.items.Count ; i++)
            {
                string key = list.items[i];
                GameObject select = NGUITools.AddChild(container,pre_select);
                select.name = "option" + i;
                select.transform.localPosition = new Vector3(0, start, 0);
                Transform trans = select.transform.FindChild("Label");
                UILabel label = trans.GetComponent<UILabel>();
                label.text = key;
                label.color = list.value.Equals(key) ? Color.red : Color.black;
                UIButton button = trans.FindChild("event").GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this, "select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = label;
                button.onClick.Add(event_delegate);
                start -= 100;
            }
            scroll_drag.scrollView = ob_temp.GetComponent<UIScrollView>();
        }else{
            GameObject container  = transform.FindChild("container").gameObject;
            int len = 100;
            len += list.items.Count * 100;
            back.height = len + 20;
            float start = list.items.Count / 2 * 100 - (list.items.Count % 2 == 0 ? 100 : 50);
            for (int i = 0; i < list.items.Count; i++ )
            {
                string key = list.items[i];
                GameObject select = NGUITools.AddChild(container,pre_select);
                select.name = "option" + i;
                select.transform.localPosition = new Vector3(0,start,0);
                Transform trans = select.transform.FindChild("Label");
                UILabel label = trans.GetComponent<UILabel>();
                label.text = key;
                label.color = list.value.Equals(key) ? Color.red : Color.black;
                UIButton button = trans.FindChild("event").GetComponent<UIButton>();
                EventDelegate event_delegate = new EventDelegate(this,"select");
                event_delegate.parameters[0] = new EventDelegate.Parameter();
                event_delegate.parameters[0].obj = label;
                button.onClick.Add(event_delegate);
                start -= 100;
            }
        }
    }

    public void select(UILabel label)
    {
        temp.value = label.text;
        close();
        scroll_drag.scrollView = null;
    }

    public void OnlyPop(string title_str , MainData.BankAccount accounts)
    {
		if (pre_text_label == null)
		{
			pre_text_label = Resources.Load<GameObject>("prefabs/textLabel");
		}
        panel.alpha = 0.1f;
        gameObject.SetActive(true);
        CameraUtil.push(5,3);
        if (title_str != null)
        {
            UILabel label_title = transform.FindChild("up").FindChild("title").GetComponentInChildren<UILabel>();
            label_title.text = title_str;
        }
        UITexture back = transform.FindChild("back").GetComponent<UITexture>();
        if (accounts.names.Count > 16)
        {//需要滚屏
            back.height = 820;
            int len = 50;
            if (pre_container == null)
            {
                pre_container = Resources.Load<GameObject>("prefabs/popup-container");
            }
            GameObject ob_temp = NGUITools.AddChild(transform.FindChild("container").gameObject, pre_container);
            ob_temp.name = "body";
            ob_temp.transform.localPosition = new Vector3(0,-40, 0);
            GameObject container = ob_temp.transform.FindChild("container").gameObject;
            float start = 320;
            for (int i = 0; i < accounts.names.Count; i++)
            {
				GameObject sun = NGUITools.AddChild(container,pre_text_label);
                sun.name = "sun" + i;
				sun.transform.localPosition = new Vector3(-325, start, 0);
                UILabel label = sun.GetComponent<UILabel>();
                label.text = accounts.names[i] + ":" + accounts.accounts[i];
                label.alignment = NGUIText.Alignment.Center;
                label.width = 650;
                label.height = 40;
                label.color = Color.black;
                start -= len;
            }
            scroll_drag.scrollView = ob_temp.GetComponent<UIScrollView>();
        }
        else
        {
            GameObject container = transform.FindChild("container").gameObject;
            int space = 50;
            back.height = 100 + accounts.names.Count * space;
            float start = accounts.names.Count / 2 * space - (accounts.names.Count % 2 == 0 ? space : space / 2) - 20;
            for (int i = 0; i < accounts.names.Count; i++)
            {
				GameObject sun = NGUITools.AddChild(container,pre_text_label);
                sun.name = "sun" + i;
                sun.transform.localPosition = new Vector3(-325,start,0);
                UILabel label = sun.GetComponent<UILabel>();
                label.text = accounts.names[i] + ":" + accounts.accounts[i];
                label.alignment = NGUIText.Alignment.Center;
                label.width  = 650;
                label.height = 40;
                label.color = Color.black;
                start -= space;
            }
        }
    }
}
