package http.tasktracksystem.domain.utils.responses;

public final class GeneralTemplates {

    // TASK GROUP
    public static final String CREATE_TASK_GROUP = "SUCCESSFULLY CREATED TASK GROUP";

    // TASKS
    public static final String CREATE_TASK = "SUCCESSFULLY CREATED TASK";
    public static final String ASSIGN_TASK = "SUCCESSFULLY ASSIGNED TASK";
    public static final String UPDATE_TASK = "SUCCESSFULLY UPDATED TASK";
    public static final String ADD_TASK_TO_GROUP = "SUCCESSFULLY ADDED TASK TO GROUP";
    public static final String REGISTER_USER = "SUCCESSFULLY REGISTERED USER";

    // USERS
    public static final String DELETE_USER = "SUCCESSFULLY DELETED USER";
    public static final String CHANGE_ROLE_TO_USER = "SUCCESSFULLY CHANGED ROLE/ROLES TO USER";

    // FIELDS
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ROLES = "ROLES";
    public static final String STATUS = "STATUS";
    public static final String USERNAME = "USERNAME";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String TITLE = "TITLE";
    public static final String UPDATED_AT = "UPDATED_AT";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String ASSIGNED_TO = "ASSIGNED_TO";
    public static final String ADDED_TASK_TO_GROUP_BY = "ADDED_TASK_TO_GROUP_BY";
    public static final String TASK_ID = "TASK_ID";
    public static final String TASK_TITLE = "TASK_TITLE";
    public static final String TASK_GROUP_ID = "TASK_GROUP_ID";
    public static final String TASK_NAME = "TASK_NAME";
    public static final String TASK_CREATED_BY = "TASK_CREATED_BY";
    public static final String DELETED_USER_ID = "DELETED_USER_ID";
    public static final String DELETED_BY = "DELETED_BY";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String NOT_PRESENT_VALUE = "NOT_PRESENT_VALUE";
    public static final String CHANGED_BY = "CHANGED_BY";
    public static final String USER_ID = "USER_ID";

    //EXCEPTIONS
    public static final String USER_USERNAME_NOT_FOUND = "User with USERNAME: '%s' was not found.";
    public static final String USER_OR_PASSWORD_ARE_NOT_VALID = "Username or password is not valid!";
    public static final String USER_ID_NOT_FOUND = "User with ID: '%s' was not found.";
    public static final String USER_USERNAME_EMAIL_EXISTS = "USERNAME: '%s' or EMAIL: '%s' already exists.";
    public static final String TASK_ID_NOT_FOUND = "Task with ID: '%s' was not found.";
    public static final String ROLE_NOT_FOUND = "Role: '%s' was not found.";
    public static final String TASK_GROUP_ALREADY_EXISTS = "Task group: '%s' already exists.";
    public static final String TASK_GROUP_ID_NOT_FOUND = "Task group ID: '%s' was not found.";
    public static final String ROLES_NOT_FOUND = "Provided roles are not found.";

    // LOGS
    public static final String UNEXPECTED_ERROR = "Opss.. Unexpected error occurred. Sorry for the inconvenience.";

}
