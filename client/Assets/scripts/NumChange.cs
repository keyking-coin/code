using UnityEngine;
using System.Collections;

public class NumChange : MonoBehaviour {

    public UILabel label;

    public int min;

    public int max;

    public bool flag = false;

    string save;

	// Use this for initialization
	void Start () {
        save = label.text;
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void add()
    {
        int num = int.Parse(label.text);
        int len = flag ? min : 1;
        num += len;
        if (num >= max)
        {
            num = min;
        }
        label.text = MyUtilTools.numToString(num);
    }

    public void rec()
    {
        int num = int.Parse(label.text);
        int len = flag ? min : 1;
        num -= len;
        if (num < min)
        {
            num = max;
        }
        label.text = MyUtilTools.numToString(num);
    }

    public void check()
    {
        int num = int.Parse(label.text);
        if (num >= max || num < min)
        {
            return;
        }
        save = label.text;
    }
}
