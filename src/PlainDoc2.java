/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Special class to export insertString on PlainDocument
 * @author Administrator
 */
class PlainDoc2 extends PlainDocument
{
    public void insertString2(int offs, String str, AttributeSet a) throws BadLocationException
    {
        super.insertString(offs, str, a);
    }
}
