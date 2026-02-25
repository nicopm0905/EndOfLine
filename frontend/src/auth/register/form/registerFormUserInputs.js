import { formValidators } from "../../../validators/formValidators";

export const registerFormUserInputs = [
  {
    tag: "Username",
    name: "username",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Password",
    name: "password",
    type: "password",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "First Name",
    name: "firstName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Last Name",
    name: "lastName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Email",
    name: "email",
    type: "email",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator, formValidators.emailValidator],
  },
  {
    tag: "Avatar",
    name: "avatarId",
    type: "select",
    values: ["1", "2", "3", "4", "5", "6", "7"],
    defaultValue: "1",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
];
