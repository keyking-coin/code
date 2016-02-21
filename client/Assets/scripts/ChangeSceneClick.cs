using UnityEngine;
using System.Collections;

public class ChangeSceneClick : MonoBehaviour {

	public string to;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	public void click(){
		Application.LoadLevel(to);
	}
}
 
 
