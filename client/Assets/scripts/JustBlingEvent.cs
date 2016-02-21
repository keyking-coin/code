using UnityEngine;
using System.Collections;

public class JustBlingEvent : MonoBehaviour
{
    public static bool bling = false;

    int blingCount = 0;

    UITexture image;

	// Use this for initialization
	void Start () {
        image = GetComponent<UITexture>();
	}
	
	// Update is called once per frame
	void Update () {
        bling = MainData.instance.user.haveNewEmail();
        if (bling && image != null)
        {
            blingCount++;
            if (blingCount % 15 == 0)
            {
                if (image.color.Equals(Color.black))
                {
                    image.color = Color.red;
                }
                else
                {
                    image.color = Color.black;
                }
            }
        }
	}
}
