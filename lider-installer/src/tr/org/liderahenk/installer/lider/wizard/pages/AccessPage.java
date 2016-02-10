package tr.org.liderahenk.installer.lider.wizard.pages;

import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;

public interface AccessPage {
	public NextPageEventType getNextPageEventType();

	public NextPageEventType setNextPageEventType(NextPageEventType nextPageEventType);
}
