using UnityEngine;
using System.Collections.Generic;

public class UpEditClickEvent : MonoBehaviour{

    public GameObject up_edit;
    public GameObject sun_list;
    public string up_show_name;
    public string sun_show_name;

	// Use this for initialization
	void Start () {
        transform.GetComponent<UIButton>().tweenTarget = null;
	}
	
	// Update is called once per frame
	void Update () {

	}

    public  void click()
    {
        for (int i = 0 ; i < up_edit.transform.childCount ; i++)
        {
            GameObject up_child = up_edit.transform.GetChild(i).gameObject;
            float x = up_child.transform.localPosition.x;
            float z = up_child.transform.localPosition.z;
            if (up_child.name.Equals(up_show_name))
            {
                up_child.transform.localPosition = new Vector3(x,-25,z);
                up_child.transform.GetComponent<UITexture>().color = new Color(93f/255f,154f/255f,106f/255f);
            }
            else
            {
                up_child.transform.localPosition = new Vector3(x,26,z);
                up_child.transform.GetComponent<UITexture>().color = Color.gray;
            }
        }
        for (int i = 0; i < sun_list.transform.childCount; i++)
        {
            GameObject sun_child = sun_list.transform.GetChild(i).gameObject;
            if (sun_child.name.Equals(sun_show_name))
            {
                sun_child.SetActive(true);
            }
            else
            {
                sun_child.SetActive(false);
            }
        }
    }
}
