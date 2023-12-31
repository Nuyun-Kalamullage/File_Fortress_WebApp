package edu.sltc.vaadin.views.admindashboard;

import ch.qos.logback.core.Context;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import edu.sltc.vaadin.services.CurrentWifiHandler;
import edu.sltc.vaadin.timer.SimpleTimer;
import edu.sltc.vaadin.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;


@PageTitle("Admin Dashboard")
@Route(value = "admin_dashboard", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@JsModule("./adminDashboard.js")
public class AdminDashboardView extends VerticalLayout {
    private Div timerLayout;
    private TextField WifiTextField, ServerUrlTextField, JoinedStudentsTextField, submissionCountTextField;
    private UI ui;
    private Timer timer;
    public AdminDashboardView() {
        FormLayout formLayoutOne = new FormLayout();
        add(formLayoutOne);
        H2 remainingTime = new H2("Remaining Time");
        H2 otpViewer = new H2("Current OTP");
//        layout.add(remainingTime);
        formLayoutOne.add(remainingTime);
        formLayoutOne.setColspan(remainingTime, 2);
        formLayoutOne.add(otpViewer);
        formLayoutOne.setColspan(otpViewer, 2);
        Div timer1 = createTimerLayout();
        formLayoutOne.add(timer1);
        formLayoutOne.setColspan(timer1, 2);
//        layout.add(otpViewer);
        Div otp1 = otpViewer();
        formLayoutOne.add(otp1);
        formLayoutOne.setColspan(otp1, 1);
        formLayoutOne.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 4));
        setSpacing(false);

        Div moduleDetails = new Div();
        moduleDetails.setMaxWidth("800px");
        moduleDetails.addClassNames(Margin.Top.SMALL, Margin.Bottom.LARGE);
        add(moduleDetails);

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("600px");
        moduleDetails.add(formLayout);

        WifiTextField = new TextField("Connected Wifi Network");
        WifiTextField.setReadOnly(true);
        formLayout.add(WifiTextField);

        ServerUrlTextField = new TextField("Server URL");
        ServerUrlTextField.setReadOnly(true);
        formLayout.add(ServerUrlTextField);

        JoinedStudentsTextField = new TextField("Joined Students");
        JoinedStudentsTextField.setReadOnly(true);
        formLayout.add(JoinedStudentsTextField);

        submissionCountTextField = new TextField("Answer Submission Count");
        submissionCountTextField.setReadOnly(true);
        formLayout.add(submissionCountTextField);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("350px", 2));

        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        WifiTextField.setValue(CurrentWifiHandler.getWifiSSID());
        ServerUrlTextField.setValue("https://" + CurrentWifiHandler.getWlanIpAddress().get(CurrentWifiHandler.getWifiDescription()) + ":80" );
        JoinedStudentsTextField.setValue("25");
        submissionCountTextField.setValue("10");
        ui = UI.getCurrent();
        Timer wifiTimer = new Timer(true);
//        wifiTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                ui.access(new Command() {
//                    @Override
//                    public void execute() {
//                        WifiTextField.setValue(CurrentWifiHandler.getWifiSSID());
//                        ServerUrlTextField.setValue("https://" + CurrentWifiHandler.getWlanIpAddress().get(CurrentWifiHandler.getWifiDescription()) + ":4444" );
//                        // Set push mode to MANUAL to enable background updates
//                        ui.getPushConfiguration().setPushMode(PushMode.MANUAL);
//                        // Push an empty update to trigger a background refresh
//                        ui.push();
//                        // Set push mode back to AUTOMATIC (optional)
//                        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
//                    }
//                });
//            }
//        },0,5000);

    }
    //    private Div createTimerLayout() {
//        Div layout = new Div();
//        layout.getStyle().set("font-size", "30px");
//        layout.getStyle().set("color", "#333");
//        layout.getStyle().set("margin-top", "10px");
//        layout.getStyle().set("margin-bottom", "20px");
//        layout.getStyle().set("padding-left", "55px");
//        layout.getStyle().set("padding-right", "55px");
//        layout.getStyle().set("padding-top", "25px");
//        layout.getStyle().set("padding-bottom", "25px");
//        layout.getStyle().set("border", "5px solid white");
//        layout.getStyle().set("border-radius", "25px");
//        SimpleTimer timer = getRemainingTimerLayout();
//        timer.getStyle().setColor("white");
//        timer.setFractions(false);
//        timer.setHours(true);
//        timer.setMinutes(true);
//        timer.setCountUp(false);
//        timer.start();
//        layout.add(timer);
//        return layout;
//    }
    private Div firsrDivFlexbox(){
        Div layout = new Div();
        layout.getStyle().set("font-size", "30px");
        layout.getStyle().set("color", "#333");
        layout.getStyle().set("margin-top", "10px");
        layout.getStyle().set("margin-bottom", "20px");
        layout.getStyle().set("padding-left", "55px");
        layout.getStyle().set("padding-right", "55px");
        layout.getStyle().set("padding-top", "25px");
        layout.getStyle().set("padding-bottom", "25px");
        layout.getStyle().set("border", "5px solid white");
        layout.getStyle().set("border-radius", "25px");
        return layout;
    }

    private Div createTimerLayout() {
        Div layout = firsrDivFlexbox();
        SimpleTimer timer = getRemainingTimerLayout();
        timer.getStyle().setColor("white");
        timer.setFractions(false);
        timer.setHours(true);
        timer.setMinutes(true);
        timer.setCountUp(false);
        timer.start();
        layout.add(timer);
        return layout;
    }
    private Div otpViewer(){
        Div layout =firsrDivFlexbox();
        return layout ;
    }
    private SimpleTimer getRemainingTimerLayout() {
        // Calculate the remaining time and return it as a string
//        // Define the target date and time
//        LocalDateTime targetDateTime = LocalDateTime.of(2023, 10, 31, 23, 30);

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the target date and time
        // Add 3 hours to the current time
        LocalDateTime targetDateTime = currentDateTime.plusHours(3);

        // Calculate the difference between the current and target date and time
        long days = ChronoUnit.DAYS.between(currentDateTime, targetDateTime);
        long hours = ChronoUnit.HOURS.between(currentDateTime, targetDateTime);
        long minutes = ChronoUnit.MINUTES.between(currentDateTime, targetDateTime);
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, targetDateTime);

        // Return the remaining time as a string
//        return String.format("%02d",hours%24) + " " + String.format("%02d",seconds%60+1) ;
        return new SimpleTimer(seconds);
    }

}
