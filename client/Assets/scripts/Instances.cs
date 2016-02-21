using System;
using UnityEngine;
using System.Collections;

public class Instances
{
    private static Instances instance = new Instances();

    GameObject load_obj = null;

    public static Instances Value
    {
        get
        {
            return instance;
        }
    }
}
