/**
 * 
 */
package io.crs.mws.client.app.auth.signup;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextBox;
import io.crs.mws.client.core.resources.ThemeParams;
import io.crs.mws.client.core.security.AppData;
import io.crs.mws.shared.dto.auth.SignUpRequest;

/**
 * @author robi
 *
 */
public class SignupView extends ViewWithUiHandlers<SignupUiHandlers>
		implements SignupPresenter.MyView, Editor<SignUpRequest> {
	private static Logger logger = Logger.getLogger(SignupView.class.getName());

	interface Binder extends UiBinder<Widget, SignupView> {
	}

	interface Driver extends SimpleBeanEditorDriver<SignUpRequest, SignupView> {
	}

	private final Driver driver;

	@UiField
	MaterialRow container;
	
	@UiField
	MaterialPanel headerPanel;

	@UiField
	HTMLPanel brandPanel;

	@UiField
	MaterialTextBox nickname, email, password;

	@Ignore
	@UiField
	MaterialTextBox confirmpsw;

	@Inject
	SignupView(Binder uiBinder, Driver driver, ThemeParams themeParams, AppData appData) {
		logger.info("SignupView()");
		initWidget(uiBinder.createAndBindUi(this));
		MaterialLink l;

		this.driver = driver;
		driver.initialize(this);

		headerPanel.getElement().getStyle().setBackgroundColor(themeParams.getPrimaryColor());
		container.getElement().getStyle().setBackgroundColor(themeParams.getPrimaryLightColor());
		brandPanel.add(new HTML(appData.getName()));
	}

	@Override
	public void start(SignUpRequest signUpRequest) {
		driver.edit(signUpRequest);

		Scheduler.get().scheduleDeferred(() -> nickname.setFocus(true));
	}

	@UiHandler("submit")
	void onSubmitClicked(ClickEvent event) {
		SignUpRequest dto = driver.flush();
		getUiHandlers().signUp(dto);
	}
}