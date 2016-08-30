package com.joymeng.slg.union.impl;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.union.UnionBody;

public class UnionInviteReport implements Instances {
	long inviteRoleId;
	String inviteRoleName;
	RoleIcon inviteIcon = new RoleIcon();
	long unionId;
	String unionName;
	String unionIcon;
	long unionfight;
	int unionCurrentMemberNum;
	int unionMemberMaxNum;
	String unionLanguage = "CN";

	public UnionInviteReport() {

	}

	public UnionInviteReport(Role role, UnionBody union) {
		inviteRoleId = role.getId(); // 用户UID
		inviteRoleName = role.getName(); // 用户名字
		inviteIcon.copy(role.getIcon());
		unionId = union.getId();
		unionName = union.getName();
		unionIcon = union.getIcon();
		unionfight = union.getFight() / 1000 + (union.getFight() / 1000.0 > 0 ? 1 : 0);
		unionCurrentMemberNum = union.getMembers().size();
		unionMemberMaxNum = union.getMemberMaxNum();
		unionLanguage = union.getLanguage();
	}

	public long getInviteRoleId() {
		return inviteRoleId;
	}

	public void setInviteRoleId(long inviteRoleId) {
		this.inviteRoleId = inviteRoleId;
	}

	public String getInviteRoleName() {
		return inviteRoleName;
	}

	public void setInviteRoleName(String inviteRoleName) {
		this.inviteRoleName = inviteRoleName;
	}

	public RoleIcon getInviteIcon() {
		return inviteIcon;
	}

	public void setInviteIcon(RoleIcon inviteIcon) {
		this.inviteIcon = inviteIcon;
	}

	public long getUnionId() {
		return unionId;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public String getUnionIcon() {
		return unionIcon;
	}

	public void setUnionIcon(String unionIcon) {
		this.unionIcon = unionIcon;
	}

	public long getUnionfight() {
		return unionfight;
	}

	public void setUnionfight(long unionfight) {
		this.unionfight = unionfight;
	}

	public int getUnionCurrentMemberNum() {
		return unionCurrentMemberNum;
	}

	public void setUnionCurrentMemberNum(int unionCurrentMemberNum) {
		this.unionCurrentMemberNum = unionCurrentMemberNum;
	}

	public int getUnionMemberMaxNum() {
		return unionMemberMaxNum;
	}

	public void setUnionMemberMaxNum(int unionMemberMaxNum) {
		this.unionMemberMaxNum = unionMemberMaxNum;
	}

	public String getUnionLanguage() {
		return unionLanguage;
	}

	public void setUnionLanguage(String unionLanguage) {
		this.unionLanguage = unionLanguage;
	}
}
