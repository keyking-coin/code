using UnityEngine;
using System.Collections;

public class MainAdjustor : MonoBehaviour
{

	//Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {

	}

    void Awake()
    {
        float scalx = (float)Screen.width / 800;
        float scaly = (float)Screen.height / 1280;
        transform.localScale = new Vector3(scalx,scaly,1f);
    }
}
 
