using UnityEngine;
using System.Collections;

public class CheckLogin : MonoBehaviour
{

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
        if (MainData.instance.user.login())
        {
            gameObject.SetActive(false);
        }
	}

    public void openLogin(GameObject obj1 , GameObject obj2)
    {
        obj1.SetActive(false);
        obj2.SetActive(true);
    }
}
