using UnityEngine;
using System.Collections;

public class OpenDialogInput : MonoBehaviour {

    public GameObject dialogInput;

    public GameObject link_scroll;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public void click()
    {
        dialogInput.SetActive(true);
        link_scroll.SetActive(true);
    }
}
 
