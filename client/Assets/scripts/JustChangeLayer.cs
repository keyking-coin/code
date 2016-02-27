using UnityEngine;
using System.Collections;

public class JustChangeLayer : MonoBehaviour {
    int layer = 5;
    GameObject target_obj;
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        run();
	}

    public void change(int l,GameObject target = null)
    {
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
        MyUtilTools.ChangeLayer(target_obj, layer);
    }
}
