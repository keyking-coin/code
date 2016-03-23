using UnityEngine;
using System.Collections;

public class UerInfoRefresh : MonoBehaviour {

    public UserInfoEvent infoEvent;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update (){
        if (MainData.instance.user.needRefresh)
        {
            infoEvent.refreshInfo();
            MainData.instance.user.needRefresh = false;
        }
	}
}
