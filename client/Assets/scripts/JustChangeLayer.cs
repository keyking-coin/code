using UnityEngine;
using System.Collections;

public class JustChangeLayer : MonoBehaviour {

    int count = 0;

    int layer = 5;

    GameObject target_obj;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        run();
	}

    public void change(int c,int l,GameObject target = null)
    {
        count = c;
        layer = l;
        if (target == null)
        {
            target_obj = gameObject;
        }
        else
        {
            target_obj = target;
        }
    }

    public void run()
    {
        if (count > 0)
        {
            MyUtilTools.ChangeLayer(target_obj,layer);
            count--;
        }
    }
}
