/**
 * 
 */
package io.crs.mws.server.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import io.crs.mws.server.entity.GlobalConfig;
import io.crs.mws.server.model.RegistrationForm;
import io.crs.mws.server.security.local.LocalUser;
import io.crs.mws.server.security.registration.OnRegistrationCompleteEvent;
import io.crs.mws.server.service.AccountService;
import io.crs.mws.shared.exception.EntityValidationException;
import io.crs.mws.shared.exception.UniqueIndexConflictException;

/**
 * @author robi
 *
 */
@Controller
public class AppController {
	private static final Logger logger = LoggerFactory.getLogger(AppController.class);

	private final AccountService accountService;

	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	AppController(AccountService accountService, ApplicationEventPublisher eventPublisher) {
		logger.info("AppController()");
		this.accountService = accountService;
		this.eventPublisher = eventPublisher;
	}

	@ModelAttribute(value = "globalConfig")
	public GlobalConfig construct() {
		return new GlobalConfig();
	}

	@RequestMapping("/oauth2/redirect")
	public String redirect(Model model, @RequestParam(name = "token") String token) { 
		logger.info("redirect()");
		logger.info("redirect()->token=" + token);
		// ,@RequestParam(name = "error") String error 
		model.addAttribute("token", token);
		// model.addAttribute("error", error);
		return "redirect";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("registration", new RegistrationForm());
		return "signup";
	}

	@PostMapping("/registration")
	public String registration(@ModelAttribute RegistrationForm registration, WebRequest request) {
		try {
			LocalUser localUser = (LocalUser) accountService.registerAccount(registration);
			try {
				String appUrl = request.getContextPath();
				eventPublisher.publishEvent(
						new OnRegistrationCompleteEvent(localUser.getUserId(), request.getLocale(), appUrl));
//				Locale locale = request.getLocale();
				return "login";
			} catch (Exception e) {
				e.printStackTrace();
				return "signup";
			}
		} catch (EntityValidationException e) {
			e.printStackTrace();
			return "signup";
		} catch (UniqueIndexConflictException e) {
			e.printStackTrace();
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			return "signup";
		}
	}

	@RequestMapping(value = "/activate/{token}", method = GET)
	@ResponseBody
	public Boolean activate(@PathVariable String token) {
		try {
			accountService.activate(token);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/resetpsw/{token}", method = GET)
	@ResponseBody
	public Boolean resetPsw(@PathVariable String token) {
		try {
			accountService.resetPassword(token);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
}
