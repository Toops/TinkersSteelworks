package tsteelworks.structure;

public interface IStructure {
	public void validateStructure(final int x, final int y, final int z);

	public boolean isValid();

	public int getNbLayers();
}
