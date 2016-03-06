using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class RankEvent : CenterEvent {

    GameObject pref_obj;

    class RankEntity
    {
        public long uid;
        public string nikeName;
        public string face;
        public int count;
        public float worth;

        public void deserialize(ByteBuffer buffer)
        {
            uid = buffer.ReadLong();
            nikeName = buffer.ReadString();
            face = buffer.ReadString();
            count = buffer.ReadInt();
            worth = float.Parse(buffer.ReadString());
        }
    }

    List<RankEntity> entitys = new List<RankEntity>();

	// Use this for initialization
	void Start () {
        pref_obj = Resources.Load<GameObject>("prefabs/rank-entity");
	}
	
	// Update is called once per frame
	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("RankEvent");
        if (buffer != null)
        {
            entitys.Clear();
            int len = buffer.ReadInt();
            for (int i = 0 ; i < len ; i++)
            {
                RankEntity entity = new RankEntity();
                entity.deserialize(buffer);
                entitys.Add(entity);
            }
            click();
            refresh();
        }
	}

    void refresh()
    {
        Transform container = needshow[0].transform.FindChild("scroll").FindChild("body").FindChild("container");
        container.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        container.parent.localPosition = new Vector3(0, 0, 0);
        MyUtilTools.clearChild(container);
        float starty = 440;
        for (int i = 0; i < entitys.Count ; i++)
        {
            RankEntity entity = entitys[i];
            GameObject summary = NGUITools.AddChild(container.gameObject,pref_obj);
            summary.name = "rank_" + i;
            summary.transform.localPosition = new Vector3(0,starty,0);
            UISprite icon = summary.transform.FindChild("icon").GetComponent<UISprite>();
            icon.spriteName = entity.face;
            UILabel name = summary.transform.FindChild("name").GetComponent<UILabel>();
            name.text = entity.nikeName;
            UILabel context = summary.transform.FindChild("content").GetComponent<UILabel>();
            float stand = 1000000f;
            string worthStr = (entity.worth > stand ? (entity.worth / stand + "万") : (entity.worth + ""));
            context.text = "总共完成 " + entity.count + " 笔交易,成交总金额 " + worthStr;
            starty -= 130;
        }
    }

    public void open()
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("RankEvent");
        NetUtil.getInstance.SendMessage(buffer);
    }
}
