import React, { useReducer } from "react";
import { useNavigate } from "react-router";
import { api, ApiError } from "../../api/api.ts";
import "../../styles/App.css";

type State = {
  username: string;
  email: string;
  password: string;
  error: string | undefined;
  stage: "editing" | "posting" | "succeed" | "failed";
};

type Action =
    | { type: "input-change"; username: string; email: string; password: string}
    | { type: "post" }
    | { type: "success" }
    | { type: "error"; message: string };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "input-change":
      return {
        ...state,
        username: action.username,
        email: action.email,
        password: action.password,
      };
    case "post":
      return {
        ...state,
        stage: "posting",
        error: undefined,
      };
    case "success":
      return {
        username: "",
        email: "",
        password: "",
        error: undefined,
        stage: "succeed",
      };
    case "error":
      return {
        ...state,
        stage: "failed",
        error: action.message,
      };
    default:
      return state;
  }
}

const initialState: State = {
  username: "",
  email: "",
  password: "",
  error: undefined,
  stage: "editing",
};

export function Register() {
  const [state, dispatch] = useReducer(reducer, initialState);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    dispatch({ type: "post" });

    try {
      await api.createUser({
        username: state.username,
        email: state.email,
        password: state.password,
      });
      dispatch({ type: "success" });
      navigate("/login");
    } catch (err) {
      if (err instanceof ApiError) {
        dispatch({ type: "error", message: err.message });
      } else {
        dispatch({
          type: "error",
          message: "An error occurred during registration",
        });
      }
    }
  };

    return (

        <div className="auth-page"> <div className="auth-container"> <div className="auth-card"> <div className="auth-header"> <h1>Create your account</h1> <p> Start exploring the market with StockSim. </p> </div>

            <form
                onSubmit={handleSubmit}
                className="register-form"
            >
                <div className="register-form-group">
                    <label className="register-form-label">
                        Name

                        <input
                            type="text"
                            name="name"
                            value={state.username}
                            onChange={(e) =>
                                dispatch({
                                    type: "input-change",
                                    username: e.target.value,
                                    email: state.email,
                                    password: state.password,
                                })
                            }
                            required
                            autoComplete="name"
                            placeholder="Your name"
                            className="register-form-input"
                        />
                    </label>
                </div>

                <div className="register-form-group">
                    <label className="register-form-label">
                        Email address

                        <input
                            type="email"
                            name="email"
                            value={state.email}
                            onChange={(e) =>
                                dispatch({
                                    type: "input-change",
                                    username: state.username,
                                    email: e.target.value,
                                    password: state.password,
                                })
                            }
                            required
                            autoComplete="email"
                            placeholder="you@example.com"
                            className="register-form-input"
                        />
                    </label>
                </div>

                <div className="register-form-group">
                    <label className="register-form-label">
                        Password

                        <input
                            type="password"
                            name="password"
                            value={state.password}
                            onChange={(e) =>
                                dispatch({
                                    type: "input-change",
                                    username: state.username,
                                    email: state.email,
                                    password: e.target.value,
                                })
                            }
                            required
                            autoComplete="new-password"
                            placeholder="Create a password"
                            className="register-form-input"
                        />
                    </label>
                </div>

                {state.error && (
                    <div className="register-error">
                        {state.error}
                    </div>
                )}

                <button
                    type="submit"
                    disabled={state.stage === "posting"}
                    className="register-submit-btn"
                >
                    {state.stage === "posting"
                        ? "Creating account..."
                        : "Create account"}
                </button>
            </form>
        </div>

            <div className="create-account-link">
                Already have an account?{" "}
                <a href="/login">
                    Log in
                </a>
            </div>
        </div>

        </div> );
}
