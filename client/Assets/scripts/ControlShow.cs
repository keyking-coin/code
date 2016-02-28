using UnityEngine;
using System.Collections;

public class ControlShow : MonoBehaviour
{

    public GameObject link;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (link != null && !link.activeSelf)
        {
            link.SetActive(true);
        }
	}
}
 
