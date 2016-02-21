using UnityEngine;
using System.Collections.Generic;

public class CenterEvent : MonoBehaviour {

    public List<GameObject> needdisPear = new List<GameObject>();

    public List<GameObject> needshow = new List<GameObject>();

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public virtual void click()
    {
        dispear();
        show();
    }

    public void show(List<GameObject> lis , bool flag = true)
    {
        for (int i = 0; i < lis.Count; i++)
        {
            GameObject obj = lis[i];
            obj.SetActive(flag);
        }
    }

    public void show(bool flag = true)
    {
        show(needshow,flag);
    }

    public void dispear(bool flag = false)
    {
        show(needdisPear, flag);
    }

    public virtual void backToCenter()
    {
        show(false);
        dispear(true);
    }
}
 
