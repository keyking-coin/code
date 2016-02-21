using UnityEngine;
//using System.Collections;
using System.Collections.Generic;
public class PopupListLinkShow : MonoBehaviour
{
    UIPopupList list;

    public List<string> keys = new List<string>();

    public List<GameObject> values = new List<GameObject>();

    PopupListEvent popup;

    public string title = null;

	// Use this for initialization
	void Start () {
        list = gameObject.GetComponent<UIPopupList>();
        popup = GameObject.Find("base").transform.FindChild("popup-select").GetComponent<PopupListEvent>();
	}
	
	// Update is called once per frame
	void Update () {

	}

    public void check()
    {
        if (keys.Count != values.Count || list == null)
        {
            return;
        }
        int index = -1;
        for (int i = 0; i < keys.Count; i++ )
        {
            string key = keys[i];
            if (list.value.Equals(key))
            {
                index = i;
                break;
            }
        }
        if (index != -1)
        {
            for (int i = 0; i < values.Count; i++)
            {
                GameObject obj = values[i];
                if (i == index)
                {
                    obj.SetActive(true);
                }
                else
                {
                    obj.SetActive(false);
                }
            }
        }
    }

    void OnClick()
    {
        if (popup != null)
        {
            list.Close();
            popup.show(list,title);
        }
    }

    public void openSelect()
    {
        if (popup != null)
        {
            popup.show(list,title);
        }
    }

    public void doOnlyOneplace()
    {
        if (list != null && list.value.Equals("全部"))
        {
            UIPopupList link_list = transform.parent.FindChild("wjs-select").GetComponent<UIPopupList>();
            link_list.value = link_list.items[0];
            link_list = transform.parent.FindChild("address").GetComponent<UIPopupList>();
            link_list.value = link_list.items[0];
        }
    }
}
