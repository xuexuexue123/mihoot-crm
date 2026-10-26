package com.ruoyi.flowable.flow;

import com.googlecode.aviator.Expression;
import com.googlecode.aviator.code.CodeGenerator;
import com.googlecode.aviator.lexer.token.Token;
import com.googlecode.aviator.parser.Parser;
import com.googlecode.aviator.runtime.FunctionParam;

public class FakeCodeGenerator implements CodeGenerator {
    private StringBuffer sb = new StringBuffer();

    private boolean wasFirst = true;


    public void reset() {
        this.sb = new StringBuffer();
        this.wasFirst = true;
    }


    public Expression getResult() {
        return null;
    }


    public String getPostFixExpression() {
        return this.sb.toString();
    }


    public void onAdd(Token<?> lookhead) {
        this.appendToken("+");
    }


    private void appendToken(String s) {
        if (this.wasFirst) {
            this.wasFirst = false;
            this.sb.append(s);
        }
        else {
            this.sb.append(" ").append(s);
        }
    }


    public void onAndLeft(Token<?> lookhead) {

    }


    public void onAndRight(Token<?> lookhead) {
        this.appendToken("&&");

    }


    public void onJoinRight(Token<?> lookhead) {
        this.appendToken("||");
    }


    public void onTernaryBoolean(Token<?> lookhead) {

    }


    public void onTernaryLeft(Token<?> lookhead) {

    }


    public void onTernaryRight(Token<?> lookhead) {
        this.appendToken("?:");
    }

    @Override
    public void onTernaryEnd(Token<?> token) {

    }


    public void onConstant(Token<?> lookhead) {
        this.appendToken(lookhead.getLexeme());
    }


    public void onDiv(Token<?> lookhead) {
        this.appendToken("/");

    }


    public void onEq(Token<?> lookhead) {
        this.appendToken("==");

    }


    public void onGe(Token<?> lookhead) {
        this.appendToken(">=");

    }


    public void onGt(Token<?> lookhead) {
        this.appendToken(">");

    }


    public void onJoinLeft(Token<?> lookhead) {

    }


    public void onLe(Token<?> lookhead) {
        this.appendToken("<=");

    }


    public void onLt(Token<?> lookhead) {
        this.appendToken("<");

    }


    public void onMatch(Token<?> lookhead) {
        this.appendToken("=~");

    }


    public void onMod(Token<?> lookhead) {
        this.appendToken("%");

    }


    public void onMult(Token<?> lookhead) {
        this.appendToken("*");

    }

    @Override
    public void onExponent(Token<?> token) {

    }


    public void onNeg(Token<?> lookhead) {
        this.appendToken("-");

    }

    @Override
    public Expression getResult(boolean b) {
        return null;
    }


    public void onNeq(Token<?> lookhead) {
        this.appendToken("!=");

    }


    public void onNot(Token<?> lookhead) {
        this.appendToken("!");

    }


    public void onSub(Token<?> lookhead) {
        this.appendToken("-");
    }


    public void onMethodInvoke(Token<?> lookhead) {
        this.appendToken("method<invoked>");

    }

    @Override
    public void onLambdaDefineStart(Token<?> token) {

    }

    @Override
    public void onLambdaArgument(Token<?> token, FunctionParam functionParam) {

    }

    @Override
    public void onLambdaBodyStart(Token<?> token) {

    }

    @Override
    public void onLambdaBodyEnd(Token<?> token) {

    }


    public void onMethodName(Token<?> lookhead) {

    }


    public void onMethodParameter(Token<?> lookhead) {

    }


    public void onArray(Token<?> lookhead) {
        this.appendToken(lookhead.getLexeme());
    }


    public void onArrayIndexStart(Token<?> token) {

    }


    public void onArrayIndexEnd(Token<?> lookhead) {
        this.appendToken("[]");

    }


    public void onBitAnd(Token<?> lookhead) {
        this.appendToken("&");

    }


    public void onBitNot(Token<?> lookhead) {
        this.appendToken("~");

    }


    public void onBitOr(Token<?> lookhead) {
        this.appendToken("|");

    }


    public void onBitXor(Token<?> lookhead) {
        this.appendToken("^");

    }


    public void onShiftLeft(Token<?> lookhead) {
        this.appendToken("<<");

    }


    @Override
    public void onAssignment(Token<?> token) {

    }

    @Override
    public void setParser(Parser parser) {

    }

    public void onShiftRight(Token<?> lookhead) {
        this.appendToken(">>");

    }


    public void onUnsignedShiftRight(Token<?> lookhead) {
        this.appendToken(">>>");

    }

}
