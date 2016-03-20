using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using LitJson;

public class DealEvent : CenterEvent{

    public static GameObject pref_detail;

    public static GameObject pref_revert_detail;

    GameObject pref_item_summary;

    GameObject pref_button;

    public GameObject pref_order_summary;

    GameObject pref_order_detail;

    GameObject pref_order_detail_help;

    GameObject list_container;

    GameObject detail_container;

    public DealEditEvent dealEditEvent;

    public DealRevertEvent dealRevertEvent;

    public DealLookEvent dealLookEvent;

	public EventDelegate listBack = null;

    public EventDelegate detailBack = null;

    bool initFlag = false;

    public class LongParamter : Object
    {
        long _value;

        public LongParamter(long _value)
        {
            this._value = _value;
        }

        public long Value
        {
            get
            {
                return _value;
            }
        }
    }

    public class RevertDel : Object
    {
        public long id;

        public long fatherId;

        public string tips;

        public RevertDel(DealBody.Revert revert,string tips)
        {
            id = revert.id;
            fatherId = revert.dependentId;
            this.tips = tips;
        }
    }

    public class DealOrderDetailUpdate : DealOrderUpdate
    {
        DealEvent _dealEvent;

        public DealEvent dealEvent
        {
            set
            {
                _dealEvent = value;
            }
        }

        void Start(){
            transform.FindChild("revoke").GetComponent<UIButton>().onClick.Add(new EventDelegate(_revoke));
        }

        void nolook()
        {
            DialogUtil.tip("你不是交易双方无法查看", false);
        }

        void Update(){
            ByteBuffer buffer = MyUtilTools.tryToLogic("OrderRevoke");
            if (buffer != null)
            {
                DialogUtil.tip("申请成功",true);
            }
            if (order.item.flag == JustRun.DEL_FLAG)
            {//浏览的交易被删除了
                _dealEvent.backList();
                return;
            }
            if (order.refresh)
            {
                order.insterToObj(gameObject);
                UIButton button = transform.FindChild("look").GetComponent<UIButton>();
                if (order.item.uid == MainData.instance.user.id || MainData.instance.user.id == order.buyId)
                {
                    EventDelegate event_delegat = new EventDelegate(_dealEvent,"openLook");
                    event_delegat.parameters[0] = new EventDelegate.Parameter();
                    event_delegat.parameters[0].obj = new LongParamter(order.item.id);
                    event_delegat.parameters[1] = new EventDelegate.Parameter();
                    if (order.item.uid == MainData.instance.user.id)
                    {
                        event_delegat.parameters[1].obj = new LongParamter(order.buyId);
                    }
                    else if (order.buyId == MainData.instance.user.id)
                    {
                        event_delegat.parameters[1].obj = new LongParamter(order.item.uid);
                    }
                    button.onClick.Add(event_delegat);
                }
                else
                {
                    EventDelegate event_delegat = new EventDelegate(nolook);
                    button.onClick.Add(event_delegat);
                }
                GameObject buy_obj = transform.FindChild("buyer-appraise").gameObject;
                GameObject sell_obj = transform.FindChild("seller-appraise").gameObject;
                order.buyerAppraise.insterToObj(buy_obj,gameObject,transform.parent.parent.parent.gameObject);
                order.sellerAppraise.insterToObj(sell_obj,gameObject,transform.parent.parent.parent.gameObject);
                order.refresh = false;
            }
        }

        void _revoke()
        {
            ByteBuffer buffer = ByteBuffer.Allocate(1024);
            buffer.skip(4);
            buffer.WriteString("OrderRevoke");
            buffer.WriteLong(MainData.instance.user.id);
            buffer.WriteLong(order.item.id);
            buffer.WriteLong(order.id);
            NetUtil.getInstance.SendMessage(buffer);
        }
    }

    public class DealOrderUpdate : MonoBehaviour
    {
        protected DealBody.Order order;

        public DealBody.Order Order
        {
            set
            {
                order = value;
                order.refresh = true;
            }
        }

        void Start(){

        }

        void Update(){
            if (order.refresh)
            {
                UILabel label1 = transform.FindChild("title1").GetComponentInChildren<UILabel>();
                label1.text = "[ff0000]" + order.item.stampName + "[-] " + order.price + "元/" + order.item.monad;
                UILabel label2 = transform.FindChild("title2").GetComponentInChildren<UILabel>();
                string[] ss = order.item.bourse.Split(","[0]);
                label2.text = "成交数量: " + order.num + order.item.monad;
                UILabel label3 = transform.FindChild("title3").GetComponentInChildren<UILabel>();
                label3.text = (order.item.typeStr.Equals("入库") ? "文交所" : "交易地") + ": " + ss[1];
                UILabel label = transform.FindChild("state").GetComponentInChildren<UILabel>();
               //System.DateTime time = System.DateTime.Parse(order.item.validTime);
                if (order.helpflag)
                {
                    if (order.state == 0)
                    {
                        label.text = "下单";
                    }
                    else if (order.state == 1)
                    {
                        label.text = "付款";
                    }
                    else if (order.state == 2)
                    {
                        label.text = "中介";
                    }
                    else if (order.state == 3)
                    {
                        label.text = "发货";
                    }
                    else if (order.state == 4)
                    {
                        label.text = "收货";
                    }
                    else if (order.state == 5)
                    {
                        if (order.sellerAppraise.isCompleted && order.buyerAppraise.isCompleted)
                        {
                            label.text = "完成";
                        }
                        else if (order.sellerAppraise.isCompleted && !order.buyerAppraise.isCompleted)
                        {
                            label.text = "卖评";
                        }
                        else if (order.buyerAppraise.isCompleted && !order.sellerAppraise.isCompleted)
                        {
                            label.text = "买评";
                        }
                        else
                        {
                            label.text = "收款";
                        }
                    }
                }
                else
                {
                    if (order.state == 0)
                    {
                        label.text = "下单";
                    }
                    else if (order.state == 1)
                    {
                        label.text = "付款";
                    }
                    else if (order.state == 2)
                    {
                        label.text = "发货";
                    }
                    else
                    {
                        if (order.sellerAppraise.isCompleted && order.buyerAppraise.isCompleted)
                        {
                            label.text = "完成";
                        }
                        else if (order.sellerAppraise.isCompleted && !order.buyerAppraise.isCompleted)
                        {
                            label.text = "卖评";
                        }
                        else if (order.buyerAppraise.isCompleted && !order.sellerAppraise.isCompleted)
                        {
                            label.text = "买评";
                        }
                        else
                        {
                            label.text = "收货";
                        }
                    }
                }
                order.refresh = false;
            }
        }
    }

    public class DealDetailUpdate : DealUpdate
    {
        DealEvent _dealEvent = null;

        void Update()
        {
            if (dealBody.flag == JustRun.DEL_FLAG)
            {//浏览的交易被删除了
                _dealEvent.backList();
                return;
            }
            if (dealBody.refresh)
            {
                dealBody.insterItem(gameObject);
                GameObject events = transform.FindChild("event").gameObject;
                for (int i = 0 ; i < events.transform.childCount ; i ++){
                    GameObject child = events.transform.GetChild(i).gameObject;
                    child.SetActive(false);
                }
                if (MainData.instance.user.id == dealBody.uid)
                {//楼主
                    Transform trans = events.transform.FindChild("iusse");
                    trans.localPosition = new Vector3(-320,0,0);
                    UIButton button = trans.GetComponent<UIButton>();
                    EventDelegate event_delegat = new EventDelegate(_dealEvent, "checkIssue");
                    event_delegat.parameters[0] = new EventDelegate.Parameter();
                    event_delegat.parameters[0].obj = dealBody;
                    button.onClick.Add(event_delegat);
                    trans.gameObject.SetActive(true);

                    trans = events.transform.FindChild("revoke");
                    trans.localPosition = new Vector3(240,0,0);
                    button = trans.GetComponent<UIButton>();
                    event_delegat = new EventDelegate(_dealEvent,"confirmDelDeal");
                    event_delegat.parameters[0] = new EventDelegate.Parameter();
                    event_delegat.parameters[0].obj = dealBody;
                    button.onClick.Add(event_delegat);
                    trans.gameObject.SetActive(true);
                }
                else
                {
                    System.DateTime time = System.DateTime.Parse(dealBody.validTime);
                    if (time.CompareTo(System.DateTime.Now) < 0)
                    {//已失效
                        Transform trans = events.transform.FindChild("look");
                        trans.localPosition = new Vector3(-320, 0, 0);
                        UIButton button = trans.GetComponent<UIButton>();
                        EventDelegate event_delegat = new EventDelegate(_dealEvent, "openLook");
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = new LongParamter(dealBody.id);
                        event_delegat.parameters[1] = new EventDelegate.Parameter();
                        event_delegat.parameters[1].obj = new LongParamter(dealBody.uid);
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);

                        trans = events.transform.FindChild("revert");
                        trans.localPosition = new Vector3(-50, 0, 0);
                        button = trans.GetComponent<UIButton>();
                        event_delegat = new EventDelegate(_dealEvent, "openRevert");
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = dealBody;
                        event_delegat.parameters[1] = new EventDelegate.Parameter();
                        event_delegat.parameters[1].obj = new LongParamter(dealBody.uid);
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);

                        if (MainData.instance.user.isFavorite(dealBody.id))
                        {
                            trans = events.transform.FindChild("favorite-cancle");
                            event_delegat = new EventDelegate(_dealEvent, "cancleFavorite");
                            trans.localPosition = new Vector3(240,0,0);
                        }
                        else
                        {
                            trans = events.transform.FindChild("favorite-sure");
                            event_delegat = new EventDelegate(_dealEvent, "favorite");
                            trans.localPosition = new Vector3(240,-5,0);
                        }
                        
                        button = trans.GetComponent<UIButton>();
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = dealBody;
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);
                    }
                    else
                    {
                        Transform trans = events.transform.FindChild("grab");
                        trans.localPosition = new Vector3(-320, 0, 0);
                        UIButton button = trans.GetComponent<UIButton>();
                        EventDelegate event_delegat = new EventDelegate(_dealEvent,"openGrab");
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = dealBody;
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);

                        trans = events.transform.FindChild("look");
                        trans.localPosition = new Vector3(-140, 0, 0);
                        button = trans.GetComponent<UIButton>();
                        event_delegat = new EventDelegate(_dealEvent, "openLook");
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = new LongParamter(dealBody.id);
                        event_delegat.parameters[1] = new EventDelegate.Parameter();
                        event_delegat.parameters[1].obj = new LongParamter(dealBody.uid);
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);

                        trans = events.transform.FindChild("revert");
                        trans.localPosition = new Vector3(60,0,0);
                        button = trans.GetComponent<UIButton>();
                        event_delegat = new EventDelegate(_dealEvent, "openRevert");
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = dealBody;
                        event_delegat.parameters[1] = new EventDelegate.Parameter();
                        event_delegat.parameters[1].obj = new LongParamter(dealBody.uid);
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);

                        if (MainData.instance.user.isFavorite(dealBody.id))
                        {
                            trans = events.transform.FindChild("favorite-cancle");
                            event_delegat = new EventDelegate(_dealEvent,"cancleFavorite");
                        }
                        else
                        {
                            trans = events.transform.FindChild("favorite-sure");
                            event_delegat = new EventDelegate(_dealEvent,"favorite");
                        }
                        trans.localPosition = new Vector3(240, 0, 0);
                        button = trans.GetComponent<UIButton>();
                        event_delegat.parameters[0] = new EventDelegate.Parameter();
                        event_delegat.parameters[0].obj = dealBody;
                        button.onClick.Add(event_delegat);
                        trans.gameObject.SetActive(true);
                    }
                }
                _dealEvent._refresh(dealBody,transform.FindChild("reverts").gameObject);
                dealBody.refresh = false;
            }
            ByteBuffer buffer = MyUtilTools.tryToLogic("DealFavorite");
            if (buffer != null)
            {
                int type = buffer.ReadInt();
                int len = buffer.ReadInt();
                MainData.instance.user.favorites.Clear();
                for (int i = 0; i < len; i++)
                {
                    long value = buffer.ReadLong();
                    MainData.instance.user.favorites.Add(value);
                }
                DialogUtil.tip(type == 0 ? "收藏成功":"取消收藏成功",true);
            }
        }

        public DealEvent dealEvent
        {
            set
            {
                _dealEvent = value;
            }
        }
    }

    public class DealUpdate : MonoBehaviour
    {
        protected DealBody dealBody;
        
        public DealBody Body
        {
            set
            {
                dealBody = value;
                dealBody.refresh = true;
            }
        }

        void Start(){

        }

        void Update(){
            if (dealBody.refresh)
            {
                UILabel label1 = transform.FindChild("title1").GetComponent<UILabel>();
                label1.text = "[ff0000]" + dealBody.stampName + "[-] " + dealBody.price + "元" + "/" + dealBody.monad;
                string[] ss = dealBody.bourse.Split(","[0]);
                UILabel label2 = transform.FindChild("title2").GetComponent<UILabel>();
                label2.text = "剩余数量:" + dealBody.curNum + dealBody.monad;
                UILabel label3 = transform.FindChild("title3").GetComponent<UILabel>();
                label3.text = (dealBody.typeStr.Equals("入库") ? "文交所" : "交易地") + ": " + ss[1];
                UILabel label = transform.FindChild("validTime").GetComponent<UILabel>();
                label.text = dealBody.validTime;
                System.DateTime time = System.DateTime.Parse(dealBody.validTime);
                label.color = time.CompareTo(System.DateTime.Now) < 0 ? Color.gray : Color.red;
                dealBody.refresh = false;
            }
        }
    }

    public class DealGrab : MonoBehaviour
    {
        DealEvent dealEvent = null;

        GameObject tips = null;

        GameObject do_obj = null;

        DealBody item;

        void Start () 
        {

        }

        public void init(DealBody item , DealEvent dealEvent)
        {
            this.item = item;
            this.dealEvent = dealEvent;
            tips = transform.FindChild("tips").gameObject;
            tips.SetActive(false);
            do_obj = transform.FindChild("do").gameObject;
            do_obj.SetActive(true);
            /*
            GameObject flag_obj = do_obj.transform.FindChild("flag").gameObject;
            flag_obj.SetActive(item.helpFlag);
            if (item.helpFlag)
            {
                UIButton button = flag_obj.GetComponent<UIButton>();
                button.onClick.Clear();
                UIToggle toggle = flag_obj.GetComponent<UIToggle>();
                if (!item.seller)
                {//买家选择了中介服务，卖家必须选择中介服务
                    toggle.value = true;
                    toggle.enabled = false;
                    button.onClick.Add(new EventDelegate(item.showMustUseHelpTip));
                }
                else
                {
                    toggle.enabled = true;
                }
            }
            do_obj.transform.FindChild("inputer").transform.localPosition = new Vector3(0,item.helpFlag ? 50 : 0,0);
             */
            transform.GetComponent<JustChangeLayer>().change(10);
        }

        void Update()
        {
            ByteBuffer buffer = NetUtil.getInstance.find("DealGrab");
            if (buffer != null)
            {
                NetUtil.getInstance.remove("DealGrab");
                int result = buffer.ReadInt();
                if (result == 1)
                {
                    string tip = buffer.ReadString();
                    DialogUtil.tip(tip);
                }
                else
                {
                    tips.SetActive(true);
                    do_obj.SetActive(false);
                }
            }
        }

        public void grabBack()
        {
            gameObject.SetActive(false);
            transform.parent.FindChild("body").GetComponent<UIPanel>().alpha = 1f;
            GameObject.Find("main").transform.FindChild("back").gameObject.SetActive(true);
            CameraUtil.pop(3);
            Destroy(this);
        }

        public void doGrab()
        {
            if (tips.activeSelf)
            {
                grabBack();
            }
            else
            {
                ByteBuffer buffer = ByteBuffer.Allocate(1024);
                buffer.skip(4);
                buffer.WriteString("DealGrab");
                buffer.WriteLong(item.id);
                buffer.WriteLong(MainData.instance.user.id);
                UIInput input = do_obj.transform.FindChild("inputer").GetComponent<UIInput>();
                int num = int.Parse(input.value);
                buffer.WriteInt(num);
                NetUtil.getInstance.SendMessage(buffer);
            }
        }
    }

    public class DealListRefresh : MonoBehaviour
    {
        public DealEvent dealEvent = null;
        void Update()
        {
            bool refesh = false;
            for (int i = 0; i < MainData.instance.deal_all.Count; )
            {
                DealBody deal = MainData.instance.deal_all[i];
                byte flag = deal.flag;
                deal.flag = JustRun.NULL_FLAG;
                if (flag == JustRun.DEL_FLAG)
                {
                    refesh = true;
                    MainData.instance.deal_all.RemoveAt(i);
                    continue;
                }
                if (flag == JustRun.ADD_FLAG)
                {
                    refesh = true;
                }
                i++;
            }
            if (refesh)
            {
                dealEvent.updateList();
            }
        }
    }

    public class DealDetailRefresh : MonoBehaviour
    {
        public DealEvent dealEvent = null;

        void Update()
        {
            bool flag = false;
            ByteBuffer buffer = MyUtilTools.tryToLogic("DealDel");
            if (buffer != null)
            {
                DialogUtil.tip("撤销成功",true);
            }
            buffer = MyUtilTools.tryToLogic("RevertDel");
            if (buffer != null)
            {
                DialogUtil.tip("操作成功", true);
            }
            buffer = MyUtilTools.tryToLogic("UserLook");
            if (buffer != null)
            {
                dealEvent.toLook(buffer);
            }
            buffer = MyUtilTools.tryToLogic("DealIssue");
            if (buffer != null)
            {
                 DialogUtil.tip(buffer.ReadString(),true);
            }
            if (flag)
            {
                dealEvent.updateList();
            }
        }
    }

    void init()
    {
        if (!initFlag)
        {
            pref_button = Resources.Load<GameObject>("prefabs/detail-button");
            pref_item_summary = Resources.Load<GameObject>("prefabs/deal-summary");
            pref_order_summary = Resources.Load<GameObject>("prefabs/order-summary");
            pref_detail = Resources.Load<GameObject>("prefabs/deal-detail");
            pref_revert_detail = Resources.Load<GameObject>("prefabs/revert-detail");
            pref_order_detail = Resources.Load<GameObject>("prefabs/order-detail-normal");
            pref_order_detail_help = Resources.Load<GameObject>("prefabs/order-detail-help");
            list_container = needshow[0].transform.FindChild("list").FindChild("body").FindChild("container").gameObject;
            DealListRefresh listRefresh = list_container.AddComponent<DealListRefresh>();
            listRefresh.dealEvent = this;
            detail_container = needshow[0].transform.FindChild("detail").FindChild("body").FindChild("container").gameObject;
            DealDetailRefresh detailRefresh = detail_container.AddComponent<DealDetailRefresh>();
            detailRefresh.dealEvent = this;
            EventDelegate backEvent = new EventDelegate(backAndShow);
            dealEditEvent.callback = backEvent;
            dealRevertEvent.callback = backEvent;
            dealLookEvent.callback = backEvent;
            initFlag = true;
        }
    }

	void Start () {
        init();
	}

    public void showSearchButton(bool flag)
    {
        needshow[0].transform.FindChild("list").FindChild("title").FindChild("search").gameObject.SetActive(flag);
    }

	public void initList()
	{
        click();
        init();
        needshow[0].transform.FindChild("list").gameObject.SetActive(true);
		needshow[0].transform.FindChild("pop").gameObject.SetActive(false);
		needshow[0].transform.FindChild("search").gameObject.SetActive(false);
		needshow[0].transform.FindChild("detail").gameObject.SetActive(false);
		updateList();
	}

	void Update () {
        ByteBuffer buffer = MyUtilTools.tryToLogic("EnterDeal");
        if (buffer != null)
        {
			MainData.instance.deserializeDeals(buffer);
			initList();
        }
	}


    void toLook(ByteBuffer buffer)
    {
        showSub("look");
        dealLookEvent.show(buffer);
    }

    void openLook(LongParamter dealId ,LongParamter uid)
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("UserLook");
        buffer.WriteLong(dealId.Value);
        buffer.WriteLong(uid.Value);
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void delRevertSure(RevertDel revert)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("RevertDel");
        buffer.WriteLong(revert.id);
        buffer.WriteLong(MainData.instance.user.id);
        buffer.WriteLong(revert.fatherId);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void openDelRevert(RevertDel revert)
    {
        EventDelegate sure = new EventDelegate(this,"delRevertSure");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = revert;
        ConfirmUtil.confirm("确定" + revert.tips + "此条回复内容？",sure);
    }

    void openRevert(DealBody item,LongParamter target)
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            LoginEvent.callback = new EventDelegate(this, "back_deal_detail");
            LoginEvent.callback.parameters[0] = new EventDelegate.Parameter();
            LoginEvent.callback.parameters[0].obj = item;
            return;
        }
        showSub("revert");
        //CameraUtil.push(3,2);
        dealRevertEvent.okback = new EventDelegate(backAndShow);
        dealRevertEvent.show(item, target.Value);
    }

    void openGrab(DealBody item)
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            LoginEvent.callback = new EventDelegate(this, "back_deal_detail");
            LoginEvent.callback.parameters[0] = new EventDelegate.Parameter();
            LoginEvent.callback.parameters[0].obj = item;
            return;
        }
        detail_container.transform.parent.GetComponent<UIPanel>().alpha = 0.2f;
        GameObject grab = detail_container.transform.parent.parent.FindChild("grab").gameObject;
        grab.SetActive(true);
        DealGrab grab_scritp = grab.AddComponent<DealGrab>();
        grab_scritp.init(item,this);
        Transform do_trans = grab.transform.FindChild("do");
        do_trans.FindChild("inputer").GetComponent<GrabInputEvent>().init(item.curNum);
        //逻辑部分
        UIButton button_sure = do_trans.FindChild("sure").GetComponent<UIButton>();
        button_sure.onClick.Clear();
        button_sure.onClick.Add(new EventDelegate(grab_scritp.doGrab));
        UIButton button_cancle = do_trans.FindChild("cancle").GetComponent<UIButton>();
        button_cancle.onClick.Clear();
        button_cancle.onClick.Add(new EventDelegate(grab_scritp.grabBack));
        UIButton button_close = grab.transform.FindChild("tips").FindChild("close").GetComponent<UIButton>();
        button_close.onClick.Clear();
        button_close.onClick.Add(new EventDelegate(grab_scritp.grabBack));
        CameraUtil.push(3,2);
        GameObject.Find("main").transform.FindChild("back").gameObject.SetActive(false);
    }

    void backAndShow()
    {
        needshow[0].transform.FindChild("detail").gameObject.SetActive(true);
        needshow[0].transform.FindChild("pop").gameObject.SetActive(false);
    }

    void showSub(string name)
    {
        needshow[0].transform.FindChild("detail").gameObject.SetActive(false);
        GameObject pop = needshow[0].transform.FindChild("pop").gameObject;
        pop.SetActive(true);
        for (int i = 0 ; i <  pop.transform.childCount ; i++){
            GameObject child = pop.transform.GetChild(i).gameObject;
            if (child.name.Equals(name))
            {
                child.SetActive(true);
            }
            else
            {
                child.SetActive(false);
            }
        }
    }

    void dealEdit(DealBody item)
    {
        showSub("edit");
        dealEditEvent.show(item);
    }

    void delDeal(DealBody item)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealDel");
        buffer.WriteLong(item.id);
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void confirmDelDeal(DealBody item)
    {
        EventDelegate sure = new EventDelegate(this,"delDeal");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = item;
        ConfirmUtil.confirm("确定撤销此贴？", sure);
    }

    void issueSure(DealBody item)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealIssue");
        buffer.WriteLong(item.id);
        buffer.WriteLong(item.uid);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void checkIssue(DealBody item)
    {
        if (!MainData.instance.user.recharge.haveMoney(10))
        {
            DialogUtil.tip("您的邮游币不足请先去充值");
            return;
        }
        EventDelegate sure = new EventDelegate(this,"issueSure");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = item;
        ConfirmUtil.confirm("需要消耗10个邮游币继续推送？", sure);
    }

    void _refresh(DealBody item , GameObject obj_suns)
    {
        MyUtilTools.clearChild(obj_suns.transform);
        float y = 0;
        for (int i = 0; i < item.reverts.Count; i++)
        {
            DealBody.Revert revert = item.reverts[i];
            GameObject obj_revert = NGUITools.AddChild(obj_suns,pref_revert_detail);
            obj_revert.name = "revert_" + i;
            obj_revert.transform.localPosition = new Vector3(0,y,0);
            y -= revert.update(obj_revert);
            GameObject events = obj_revert.transform.FindChild("event").gameObject;
            for (int j = 0 ; j < events.transform.childCount; j++)
            {
                GameObject child = events.transform.GetChild(j).gameObject;
                child.SetActive(false);
            }
            if (MainData.instance.user.id == revert.uid)
            {
                Transform trans = events.transform.FindChild("revoke");
                trans.localPosition = new Vector3(-50,0,0);
                UIButton button = trans.GetComponent<UIButton>();
                EventDelegate event_delegat = new EventDelegate(this,"openDelRevert");
                event_delegat.parameters[0] = new EventDelegate.Parameter();
                event_delegat.parameters[0].obj = new RevertDel(revert,"撤销");
                button.onClick.Add(event_delegat);
                trans.gameObject.SetActive(true);
            }
            else
            {
                Transform trans = events.transform.FindChild("look");
                trans.localPosition = new Vector3(-320, 0, 0);
                UIButton button = trans.GetComponent<UIButton>();
                EventDelegate event_delegat = new EventDelegate(this,"openLook");
                event_delegat.parameters[0] = new EventDelegate.Parameter();
                event_delegat.parameters[0].obj = new LongParamter(item.id);
                event_delegat.parameters[1] = new EventDelegate.Parameter();
                event_delegat.parameters[1].obj = new LongParamter(revert.uid);
                button.onClick.Add(event_delegat);
                trans.gameObject.SetActive(true);

                trans = events.transform.FindChild("revert");
                trans.localPosition = new Vector3(240,0,0);
                button = trans.GetComponent<UIButton>();
                event_delegat = new EventDelegate(this,"openRevert");
                event_delegat.parameters[0] = new EventDelegate.Parameter();
                event_delegat.parameters[0].obj = item;
                event_delegat.parameters[1] = new EventDelegate.Parameter();
                event_delegat.parameters[1].obj = new LongParamter(revert.uid);
                button.onClick.Add(event_delegat);
                trans.gameObject.SetActive(true);
            }
        }
    }

    void back_deal_detail(DealBody item)
    {
        bool flag = false;
        if (MainData.instance.user.id == item.uid)
        {
            flag = true;
        }
        if (!flag)
        {
            for (int i = 0; i < item.reverts.Count; i++)
            {
                DealBody.Revert revert = item.reverts[i];
                if (MainData.instance.user.id == revert.uid || (MainData.instance.user.id == item.uid && MainData.instance.user.id != revert.uid))
                {
                    flag = true;
                    break;
                }
            }
        }
        if (flag)
        {
            deal_detail(item);
        }
    }

    void comfirmFavorite(DealBody item)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealFavorite");
        buffer.WriteInt(0);
        buffer.WriteLong(item.id);
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void favorite(DealBody item)
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            LoginEvent.callback = new EventDelegate(this,"back_deal_detail");
            LoginEvent.callback.parameters[0] = new EventDelegate.Parameter();
            LoginEvent.callback.parameters[0].obj = item;
            return;
        }
        EventDelegate sure = new EventDelegate(this,"comfirmFavorite");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = item;
        ConfirmUtil.confirm("确定收藏？",sure);
    }

    void comfirmCancleFavorite(DealBody item)
    {
        ConfirmUtil.TryToDispear();
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("DealFavorite");
        buffer.WriteInt(1);
        buffer.WriteLong(item.id);
        buffer.WriteLong(MainData.instance.user.id);
        NetUtil.getInstance.SendMessage(buffer);
    }

    void cancleFavorite(DealBody item)
    {
        if (!MainData.instance.user.login())
        {
            LoginEvent.tryToLogin();
            LoginEvent.callback = new EventDelegate(this,"back_deal_detail");
            LoginEvent.callback.parameters[0] = new EventDelegate.Parameter();
            LoginEvent.callback.parameters[0].obj = item;
            return;
        }
        EventDelegate sure = new EventDelegate(this,"comfirmCancleFavorite");
        sure.parameters[0] = new EventDelegate.Parameter();
        sure.parameters[0].obj = item;
        ConfirmUtil.confirm("取消收藏？",sure);
    }

    void deal_detail(DealBody item)
    {
        needshow[0].transform.FindChild("detail").gameObject.SetActive(true);
        needshow[0].transform.FindChild("list").gameObject.SetActive(false);
        needshow[0].transform.FindChild("detail").FindChild("grab").gameObject.SetActive(false);
        MyUtilTools.clearChild(detail_container.transform);
        detail_container.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        detail_container.transform.parent.localPosition = new Vector3(0,-70.0f,0);
        GameObject obj_item = NGUITools.AddChild(detail_container,pref_detail);
        obj_item.name = "item";
        obj_item.transform.localPosition = new Vector3(0,520,0);
        DealDetailUpdate detailUpdate = obj_item.AddComponent<DealDetailUpdate>();
        detailUpdate.Body      = item;
        detailUpdate.dealEvent = this;
    }

    public void backList()
    {
        if (detailBack != null)
        {
            detailBack.Execute();
            detailBack = null;
            return;
        }
        needshow[0].transform.FindChild("list").gameObject.SetActive(true);
        needshow[0].transform.FindChild("detail").gameObject.SetActive(false);
        updateList();
    }

    public void mustShowDealOreder()
    {
        Transform event_trans = needshow[0].transform.FindChild("list").FindChild("up-title").FindChild("events");
        ButtonSelectEvent rbs = event_trans.FindChild("rk").GetComponent<ButtonSelectEvent>();
        ButtonSelectEvent xbs = event_trans.FindChild("xh").GetComponent<ButtonSelectEvent>();
        ButtonSelectEvent target = rbs.flag ? rbs : xbs;
        Transform sun_trans = rbs.transform.FindChild("suns");
        sun_trans.FindChild("cj").GetComponent<ButtonSelectEvent>().click2(sun_trans.FindChild("mm").GetComponent<ButtonSelectEvent>(),this);
        updateList();
    }

	public void updateList()
    {
        Transform event_trans = needshow[0].transform.FindChild("list").FindChild("up-title").FindChild("events");
        ButtonSelectEvent rbs = event_trans.FindChild("rk").GetComponent<ButtonSelectEvent>();
        ButtonSelectEvent xbs = event_trans.FindChild("xh").GetComponent<ButtonSelectEvent>();
        ButtonSelectEvent mbs = rbs.flag ? rbs.transform.FindChild("suns").FindChild("mm").GetComponent<ButtonSelectEvent>() : xbs.transform.FindChild("suns").FindChild("mm").GetComponent<ButtonSelectEvent>();
        string key = rbs.flag ? "入库" : "现货";
        list_container.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        list_container.transform.parent.localPosition = new Vector3(0,-180,0);
        if (mbs.flag)
        {//买卖盘
            List<DealBody> temp = new List<DealBody>();
			foreach (DealBody item in MainData.instance.deal_all)
            {
                if (item.typeStr.Equals(key))
                {
                    temp.Add(item);
                }
            }
            refreshListDeal(temp);
        }
        else
        {//成交盘
            List<DealBody.Order> temp = new List<DealBody.Order>();
            foreach (DealBody item in MainData.instance.deal_all)
            {
                if (item.typeStr.Equals(key))
                {
                    foreach (DealBody.Order order in item.orders)
                    {
                        if (order.checkRevoke(DealBody.Order.ORDER_REVOKE_ALL))
                        {//双方都撤销了
                            continue;
                        }
                        temp.Add(order);
                    }
                }
            }
            temp.Sort();//排序
            refreshListOrder(list_container,temp,this);
        }
    }

    public void order_detail(DealBody.Order order)
    {
        needshow[0].transform.FindChild("detail").gameObject.SetActive(true);
        needshow[0].transform.FindChild("list").gameObject.SetActive(false);
        needshow[0].transform.FindChild("detail").FindChild("grab").gameObject.SetActive(false);
        needshow[0].transform.FindChild("detail").FindChild("appraise").gameObject.SetActive(false);
        MyUtilTools.clearChild(detail_container.transform);
        detail_container.transform.parent.GetComponent<UIPanel>().clipOffset = Vector2.zero;
        detail_container.transform.parent.localPosition = new Vector3(0,-70,0);
        GameObject obj_order = NGUITools.AddChild(detail_container, order.helpflag ? pref_order_detail_help : pref_order_detail);
        obj_order.transform.localPosition = new Vector3(0,560,0);
        obj_order.name = "order-detail";
        obj_order.GetComponent<AppraiseEvent>().Order = order;
        DealOrderDetailUpdate update = obj_order.AddComponent<DealOrderDetailUpdate>();
        update.Order = order;
        update.dealEvent = this;
    }

    public void refreshListOrder(GameObject list_obj, List<DealBody.Order> orders, MonoBehaviour target_obj, string mothdName = "order_detail")
    {

        MyUtilTools.clearChild(list_obj.transform);
        if (orders.Count == 0)
        {
            list_obj.transform.parent.parent.FindChild("no-tip").gameObject.SetActive(true);
            return;
        }
        list_obj.transform.parent.parent.FindChild("no-tip").gameObject.SetActive(false);
        float starty = 480 ,len = 180 ;
        if (target_obj != this)
        {
            starty = 510;
        }
        for (int i = 0 ; i < orders.Count ; i++)
        {
            DealBody.Order order = orders[i];
            GameObject summary = NGUITools.AddChild(list_obj,pref_order_summary);
            summary.transform.localPosition = new Vector3(0,starty,0);
            summary.name = "summary" + i;
            GameObject flg_obj = order.item.seller ? summary.transform.FindChild("sell-flag").gameObject : summary.transform.FindChild("buy-flag").gameObject;
            flg_obj.SetActive(true);
            summary.AddComponent<DealOrderUpdate>().Order = order;
            UIButton button_event = summary.transform.FindChild("open").GetComponent<UIButton>();
            EventDelegate event_delegate = new EventDelegate(target_obj,mothdName);
            event_delegate.parameters[0] = new EventDelegate.Parameter();
            event_delegate.parameters[0].obj = order;
            button_event.onClick.Add(event_delegate);
            UILabel label = summary.transform.FindChild("seller").GetComponentInChildren<UILabel>();
            label.text = order.item.userName;
            label = summary.transform.FindChild("buyer").GetComponentInChildren<UILabel>();
            label.text = order.buyerName;
            label = summary.transform.FindChild("validTime").GetComponentInChildren<UILabel>();
            label.text = order.times[0];
            starty -= len;
        }
    }

    void refreshListDeal(List<DealBody> items)
    {
        MyUtilTools.clearChild(list_container.transform);
        if (items.Count == 0)
        {
            list_container.transform.parent.parent.FindChild("no-tip").gameObject.SetActive(true);
            return;
        }
        list_container.transform.parent.parent.FindChild("no-tip").gameObject.SetActive(false);
        float starty = 450 , len = 140;
        for (int i = 0 ; i < items.Count ; i++ )
        {
            DealBody item = items[i];
            GameObject summary = NGUITools.AddChild(list_container,pref_item_summary);
            summary.transform.localPosition = new Vector3(0,starty,0);
            summary.name = "summary" + i;
            GameObject flg_obj = item.seller ? summary.transform.FindChild("sell-flag").gameObject : summary.transform.FindChild("buy-flag").gameObject;
            flg_obj.SetActive(true);
            summary.AddComponent<DealUpdate>().Body = item;
            UIButton button_event = summary.transform.FindChild("open").GetComponent<UIButton>();
            EventDelegate event_delegate = new EventDelegate(this,"deal_detail");
            event_delegate.parameters[0] = new EventDelegate.Parameter();
            event_delegate.parameters[0].obj = item;
            button_event.onClick.Add(event_delegate);
            UILabel label = summary.transform.FindChild("owner").GetComponent<UILabel>();
            label.text = item.userName;
            starty -= len;
        }
	}

	public void openDeal(){
        tryToSearch("null");
        listBack = null;
	}

    public void tryToSearch(string searchStr)
    {
        ByteBuffer buffer = ByteBuffer.Allocate(1024);
        buffer.skip(4);
        buffer.WriteString("EnterDeal");
        buffer.WriteString(searchStr);
        NetUtil.getInstance.SendMessage(buffer);
    }

    /**
     * 查询部分
     */
    public void openSearch()
    {
		needshow[0].transform.FindChild("list").gameObject.SetActive(false);
        needshow[0].transform.FindChild("search").gameObject.SetActive(true);
    }

	public void backSearch()
    {
        needshow[0].transform.FindChild("search").gameObject.SetActive(false);
        needshow[0].transform.FindChild("list").gameObject.SetActive(true);
		updateList();
    }

	public override void backToCenter()
	{
        if (listBack != null) 
		{
            listBack.Execute();
            listBack = null;
		}else{
			base.backToCenter ();
		}
	}


}
 
 
