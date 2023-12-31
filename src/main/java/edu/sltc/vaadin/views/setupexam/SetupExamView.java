package edu.sltc.vaadin.views.setupexam;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.FinishedEvent;
import com.vaadin.flow.component.upload.StartedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamReceiver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import edu.sltc.vaadin.data.GenerateKeyPair;
import edu.sltc.vaadin.models.ExamModel;
import edu.sltc.vaadin.models.PublicKeyHolder;
import edu.sltc.vaadin.services.EmailSenderService;
import edu.sltc.vaadin.services.FileEncryptionService;
import edu.sltc.vaadin.views.MainLayout;
import edu.sltc.vaadin.views.about.AboutView;
import edu.sltc.vaadin.views.admindashboard.AdminDashboardView;
import edu.sltc.vaadin.views.studentdashboard.StudentDashboardView;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.time.LocalTime;

@PageTitle("Setup Exam")
@Route(value = "host_exam", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@JsModule("./fileUploader.js")
//load jquery
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js")
public class SetupExamView extends VerticalLayout {
    @Autowired
    private EmailSenderService senderService;
    @Autowired
    private InMemoryUserDetailsManager userDetailsManager;
    private TextField moduleCode, moduleName;
    private TextArea moduleDescription, studentEmailList;
    private  RadioButtonGroup<String> lateSubmission;
    private TimePicker startTimePicker, endTimePicker;
    private Button startServer;

    public SetupExamView() {
        setSpacing(false);
        ExamModel examModel = ExamModel.getInstance(); // Get the ExamModel instance
        H2 header = new H2("Exam Paper Registration");
        header.addClassNames(Margin.Top.LARGE, Margin.Bottom.MEDIUM);
        add(header);

        Div moduleDetails = new Div();
        moduleDetails.setMaxWidth("800px");
        moduleDetails.addClassNames(Margin.Top.SMALL, Margin.Bottom.LARGE);
        add(moduleDetails);

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("600px");
        moduleDetails.add(formLayout);

        moduleCode = new TextField("Module Code");
        formLayout.add(moduleCode);

        moduleName = new TextField("Module Name");
        formLayout.add(moduleName);

        moduleDescription = new TextArea("Module Description");
        moduleDescription.setHeight("100px");
        formLayout.add(moduleDescription);
        formLayout.setColspan(moduleDescription, 2);

        lateSubmission =  new RadioButtonGroup<>();
        lateSubmission.setLabel("Late Submission");
        lateSubmission.setItems("NO", "10 Minutes", "15 Minutes", "20 Minutes", "25 Minutes","30 Minutes");
        lateSubmission.setValue("15 Minutes");
        formLayout.add(lateSubmission);
        formLayout.setColspan(lateSubmission, 2);

        startTimePicker = new TimePicker("Start Time");
        endTimePicker = new TimePicker("End Time");

        endTimePicker.addClassNames(Margin.Top.SMALL, Margin.Bottom.SMALL);

        formLayout.add(startTimePicker, endTimePicker);


        studentEmailList = new TextArea("Student Emails List");
        studentEmailList.setHeight("100px");
        formLayout.add(studentEmailList);
        formLayout.setColspan(studentEmailList, 2);

        Upload upload = getUpload();
        // Add the upload component to the layout of the UI
        formLayout.add(upload);
        formLayout.setColspan(upload, 2);

        // Set ExamModel data to the view
        startServer = new Button("Start Server");
        startServer.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);
        startServer.addClickListener(e -> {
            ExamModel.serverIsRunning = !ExamModel.serverIsRunning;
            if (ExamModel.serverIsRunning){
                startServer();
                startServer.setText("Stop Server");
                startServer.addThemeVariants(ButtonVariant.LUMO_ERROR);
            }else{
                startServer.setText("Start Server");
                startServer.removeThemeVariants(ButtonVariant.LUMO_ERROR);
                startServer.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                        ButtonVariant.LUMO_SUCCESS);
            }
            System.out.println("Server State is "+ExamModel.serverIsRunning);
        });
        setExamModelData(examModel);
        add(startServer);

        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("350px", 2));

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        // Execute JavaScript code when the view is attached to the UI
        UI.getCurrent().getPage().executeJs("uploadFile();");
        //UI.getCurrent().getPage().executeJs(jsCode);
        }

    private static Upload getUpload() {
        Button uploadPDF = new Button("Upload PDF");
        Upload upload = new Upload();
        upload.setId("myVaadinUpload");
        // Access the underlying HTML element of the Upload component
        // Define the file receiver that will handle the file upload
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        upload.setReceiver(memoryBuffer);

        // Define the accepted file types. In this case, only PDF files are accepted.
        upload.setAcceptedFileTypes("application/pdf");
        Span dropLabel = new Span("Upload Exam Paper");
        upload.setDropLabel(dropLabel);
        upload.setUploadButton(uploadPDF);
        // Add a listener to the upload component that will be notified when the upload is finished
        upload.addSucceededListener(event -> {
//            FileEncryptionService.encryptFile(memoryBuffer.getInputStream(), "src/main/resources/examFile.pdf");
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User user) {
                FileEncryptionService.decryptFile(memoryBuffer.getInputStream(), "src/main/resources/examFile_"+user.getUsername()+".pdf", GenerateKeyPair.generateSharedSecret(PublicKeyHolder.getInstance().get(user.getUsername())));
            }
            // Retrieve the uploaded file from the FileReceiver
            // Create a Notification class that displays the success message
            ExamModel.getInstance().setExamPaperName(event.getFileName());
            Notification notification = Notification.show(event.getFileName()+" File uploaded successfully!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setPosition(Notification.Position.BOTTOM_END);
            notification.setDuration(2500);
        });
        return upload;
    }

    private void setExamModelData(ExamModel examModel) {
        // Check if ExamPaperName is not null and show saved details
        if (examModel.getExamPaperName() != null && false) {
            // Set ExamModel data to the view
            moduleCode.setValue(examModel.getModuleCode());
            moduleName.setValue(examModel.getModuleName());
            moduleDescription.setValue(examModel.getModuleDescription());
            lateSubmission.setValue(examModel.getLateSubmission());
            startTimePicker.setValue(examModel.getStartTime());
            endTimePicker.setValue(examModel.getEndTime());
            if (ExamModel.serverIsRunning){
                startServer.setText("Stop Server");
                startServer.addThemeVariants(ButtonVariant.LUMO_ERROR);
            }
        }
    }

    private void startServer() {
        // Implement your server start logic here
        // Obtain user input data
        String moduleCodeValue = moduleCode.getValue();
        String moduleNameValue = moduleName.getValue();
        String moduleDescriptionValue = moduleDescription.getValue();
        String lateSubmissionValue = lateSubmission.getValue();
        LocalTime startTimeValue = startTimePicker.getValue();
        LocalTime endTimeValue = endTimePicker.getValue();

        ExamModel examModel = ExamModel.getInstance();
        // Save data to the ExamModel
        examModel.setModuleCode(moduleCodeValue);
        examModel.setModuleName(moduleNameValue);
        examModel.setModuleDescription(moduleDescriptionValue);
        examModel.setLateSubmission(lateSubmissionValue);
        examModel.setStartTime(startTimeValue);
        examModel.setEndTime(endTimeValue);

        System.out.println(examModel);
        // Display success message or navigate to the student dashboard
        Notification.show("Exam details saved successfully!");

        //give access to students and have to add user to InMemoryUserDetailsManager
        String defaultPassword = "harindu123";
        senderService.sendEmail("nuyunpabasara@gmail.com", "User Password", defaultPassword);
        userDetailsManager.createUser(User.withUsername("nuyun457@gmail.com")
                .password(new BCryptPasswordEncoder().encode(defaultPassword))
                .roles("USER")
                .build());
    }
}
