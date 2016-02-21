using UnityEngine;
using System.Collections;

public class EndDialogInput : MonoBehaviour {

    public GameObject link_scroll;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}


    public void end()
    {
        gameObject.transform.parent.parent.gameObject.SetActive(false);
        link_scroll.SetActive(false);
    }
}
 
