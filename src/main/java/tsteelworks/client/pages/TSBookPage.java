package tsteelworks.client.pages;

import org.w3c.dom.Element;

import tsteelworks.client.gui.TSManualGui;

public abstract class TSBookPage
{
    protected TSManualGui manual;
    protected int side;

    public void init (TSManualGui manual, int side)
    {
        this.manual = manual;
        this.side = side;
    }

    public abstract void readPageFromXML (Element element);

    public void renderBackgroundLayer (int localwidth, int localheight)
    {
    }

    public abstract void renderContentLayer (int localwidth, int localheight);
}
