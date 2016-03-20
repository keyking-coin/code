using UnityEngine;
using System.Collections.Generic;

public class AdminSelectLink : MonoBehaviour {
    string[][] items = new string[][]{
      new string[]{"普通买家","高级买家"},
      new string[]{"普通营销员","高级营销员","金牌营销员"}
    };
	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void link(UIPopupList list)
    {
        UIPopupList selectList = gameObject.GetComponent<UIPopupList>();
        string value = selectList.value;
        List<string> strs = selectList.items;
        for (int i = 0; i < strs.Count; i++)
        {
            string str = strs[i];
            if (value.Equals(str))
            {
                list.items.Clear();
                list.value = items[i][0];
                for (int j = 0; j < items[i].Length; j++)
                {
                    list.items.Add(items[i][j]);
                }
                break;
            }
        }
    }
}
