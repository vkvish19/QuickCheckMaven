package com.vkvish19.github;

import jakarta.validation.constraints.Max;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Test
{
    public static void main(String[] args)
    {
        List<Integer> list = null;
        Object[] oa = list.toArray();
        System.out.println("oa.length = " + oa.length);
    }
}

class M
{
    private Serializable srl;

    public M()
    {
    }

    public M(Serializable srl)
    {
        this.srl = srl;
    }

    public Serializable getSrl()
    {
        return srl;
    }

    public void setSrl(Serializable srl)
    {
        this.srl = srl;
    }

    @Override
    public String toString()
    {
        return "M{" +
                "srl=" + srl +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        M m = (M) o;
        return Objects.equals(srl, m.srl);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(srl);
    }
}

class PatchPolicyResponse implements Serializable
{
    private String newPolicyId;
    private String errorMsg;

    public PatchPolicyResponse()
    {
    }

    public PatchPolicyResponse(String newPolicyId, String errorMsg)
    {
        this.newPolicyId = newPolicyId;
        this.errorMsg = errorMsg;
    }

    public String getNewPolicyId()
    {
        return newPolicyId;
    }

    public void setNewPolicyId(String newPolicyId)
    {
        this.newPolicyId = newPolicyId;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString()
    {
        return "PatchPolicyResponse{" +
                "newPolicyId='" + newPolicyId + '\'' +
                ", errorMsg=" + errorMsg +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        PatchPolicyResponse that = (PatchPolicyResponse)o;
        return Objects.equals(newPolicyId, that.newPolicyId) && Objects.equals(errorMsg, that.errorMsg);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(newPolicyId, errorMsg);
    }
}
