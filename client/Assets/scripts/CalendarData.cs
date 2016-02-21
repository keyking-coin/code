using UnityEngine;
using System.Collections;

public class CalendarData : MonoBehaviour {

    string _value;

    int _index;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

    public string Value
    {
        get
        {
            return _value;
        }
        set
        {
            _value = value;
        }
    }

    public int Index
    {
        get
        {
            return _index;
        }
        set
        {
            _index = value;
        }
    }
}
