using UnityEngine;
using System.Collections;

public class DownOpenLink : MonoBehaviour
{

	public GameObject next;

	bool isOpen = false;

    public float offset = 0;

    GameObject suns = null;

	// Use this for initialization
	void Start () {
        Transform suns_tran = transform.FindChild("suns");
        if (suns_tran != null)
        {
            suns = transform.FindChild("suns").gameObject;
        }
	}
	
	// Update is called once per frame
	void Update () {

	}


    public void closeLink()
    {
        isOpen = false;
        transform.FindChild("down").gameObject.SetActive(true);
        transform.FindChild("up").gameObject.SetActive(false);
        suns.SetActive(isOpen);
        offset = 0;
    }

    public void open(GameObject obj1 , GameObject obj2)
    {
        obj1.SetActive(false);
        obj2.SetActive(true);
		isOpen = !isOpen;
        if (offset > 0)
        {
            GameObject obj = next;
            while (obj != null)
            {
                float x = obj.transform.localPosition.x;
                float y = obj.transform.localPosition.y + (isOpen ? -1 : 1) * offset;
                obj.transform.localPosition = new Vector3(x,y,0);
                DownOpenLink link = obj.GetComponent<DownOpenLink>();
                obj = link == null ? null : link.next;
            }
        }
        if (suns != null)
        {
            suns.SetActive(isOpen);
        }
	}
}
 
 
