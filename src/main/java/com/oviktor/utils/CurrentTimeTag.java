package com.oviktor.utils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeTag extends TagSupport{
    private String format;

    public void setFormat(String format) {
        this.format = format;
    }
    @Override
    public int doStartTag() throws JspException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String currentTime = sdf.format(new Date());
            JspWriter out = pageContext.getOut();
            out.write(currentTime);
        } catch (IOException e) {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE; }

}
