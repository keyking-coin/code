using UnityEngine;
using System.Collections;

public class ShowExpressionEvent : MonoBehaviour {

    public GameObject list = null;

    public UICamera camera;

	void Start () {
        list.SetActive(false);
	}
	
	// Update is called once per frame
	void Update () {
        if (list.layer != 10)
        {
            for (int i = 0; i < list.transform.childCount; i++)
            {
                GameObject obj = list.transform.GetChild(i).gameObject;
                obj.layer = 10;
            }
            list.layer = 10;
        }
	}

    public void show()
    {
        camera.enabled = false;
        list.SetActive(true);
    }
}
 
