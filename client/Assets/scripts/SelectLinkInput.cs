using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class SelectLinkInput : MonoBehaviour {

    static string[][] names = new string[][]{
      new string[]{"工商银行","平安银行","农业银行"},
      new string[]{"平安银行","中国银行","建设银行","农业银行"},
      new string[]{"农业银行"},
      new string[]{"交通银行","浦发银行","建设银行","兴业银行"},
      new string[]{"工商银行","招商银行","建设银行"},
      /*new string[]{"建设银行"},
      new string[]{"农业银行"},
      new string[]{"招商银行","建设银行","工商银行","交通银行"},
      new string[]{"工商银行","建设银行"},
      new string[]{"工商银行","华夏银行","交通银行"},
      new string[]{"农业银行"},
      new string[]{"农业银行"},
      new string[]{"农业银行"},
      new string[]{"建设银行"},
      new string[]{"建设银行"},
      new string[]{"平安银行"},
      new string[]{"农业银行","中国银行","招商银行"},
      new string[]{"平安银行"},
      new string[]{"平安银行"},
      new string[]{"农业银行","建设银行"},*/
      new string[]{"农业银行"},
      /*new string[]{"建设银行"},
      new string[]{"建设银行","浦发银行"},
      new string[]{"交通银行","建设银行"},
      new string[]{"工商银行","建设银行"},*/
      new string[]{"建设银行","农业银行"},
      new string[]{"工商银行","华夏银行"}
    };

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void logic()
    {
        GameObject link = transform.FindChild("inputer").gameObject;
		if (link != null) 
		{
			UIPopupList selectList = transform.GetComponent<UIPopupList>();
            Transform label_tran = transform.FindChild("Label");
			if (selectList.value.Equals("其他文交所") || selectList.value.Equals("其他") || selectList.value.Equals("其他银行"))
			{
				link.SetActive(true);
                if (label_tran != null)
                {
                    label_tran.gameObject.SetActive(false);
                }
				MyUtilTools.ChangeLayer(link,gameObject.layer);
			}
			else
			{
                if (label_tran != null)
                {
                    label_tran.gameObject.SetActive(true);
                }
				link.SetActive(false);
			}
		}
    }

    public void check(GameObject obj)
    {
        UIPopupList selectList = obj.GetComponent<UIPopupList>();
        if (selectList.value.Equals("现货"))
        {
            gameObject.SetActive(true);
        }
        else
        {
            gameObject.SetActive(false);
        }
    }

    public void link(UIPopupList list)
    {
        UIPopupList selectList = gameObject.GetComponent<UIPopupList>();
        string value = selectList.value;
        List<string> strs = selectList.items;
        for (int i = 0; i < strs.Count; i++ )
        {
            string str = strs[i];
            if (value.Equals(str))
            {
                list.items.Clear();
                list.value = names[i][0];
                for (int j = 0; j < names[i].Length; j++)
                {
                    list.items.Add(names[i][j]);
                }
                break;
            }
        }
    }
}
