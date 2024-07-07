package io.supabase.exceptions;


    /**
     * Maps Supabase server errors to hints based on the status code and the contents of the error message.
     */
    public class FailureHint {

        /**
         * Best effort guess at why the exception was thrown.
         */
        public enum Reason {
            /**
             * The reason for the error could not be determined.
             */
            Unknown,

            /**
             * The client is set to run offline or the network is unavailable.
             */
            Offline,

            /**
             * The user's email address has not been confirmed.
             */
            UserEmailNotConfirmed,

            /**
             * The user's email address and password are invalid.
             */
            UserBadMultiple,

            /**
             * The user's password is invalid.
             */
            UserBadPassword,

            /**
             * The user's login is invalid.
             */
            UserBadLogin,

            /**
             * The user's email address is invalid.
             */
            UserBadEmailAddress,

            /**
             * The user's phone number is invalid.
             */
            UserBadPhoneNumber,

            /**
             * The user's information is incomplete.
             */
            UserMissingInformation,

            /**
             * The user is already registered.
             */
            UserAlreadyRegistered,

            /**
             * Server rejected due to number of requests
             */
            UserTooManyRequests,

            /**
             * The refresh token is invalid.
             */
            InvalidRefreshToken,

            /**
             * The refresh token expired.
             */
            ExpiredRefreshToken,

            /**
             * This operation requires a bearer/service key (do not include this key in a client app)
             */
            AdminTokenRequired,

            /**
             * No/invalid session found
             */
            NoSessionFound,

            /**
             * Something wrong with the URL to session transformation
             */
            BadSessionUrl,

            /**
             * An invalid authentication flow has been selected.
             */
            InvalidFlowType,

            /**
             * The SSO domain provided was not registered via the CLI
             */
            SsoDomainNotFound,

            /**
             * The SSO provider ID was incorrect or does not exist
             */
            SsoProviderNotFound
        }

        /**
         * Detects the reason for the error based on the status code and the contents of the error message.
         *
         * @param gte the GotrueException instance
         * @return the detected Reason
         */
        public static Reason detectReason(GotrueException gte) {
            if (gte.getContent().isEmpty()) {
                return Reason.Unknown;
            }

            int statusCode = gte.getStatusCode();
            String content = gte.getContent().get();

            switch (statusCode) {
                case 400:
                    if (content.contains("Invalid login")) return Reason.UserBadLogin;
                    if (content.contains("Email not confirmed")) return Reason.UserEmailNotConfirmed;
                    if (content.contains("Invalid Refresh Token")) return Reason.InvalidRefreshToken;
                    if (content.contains("Phone") || content.contains("phone")) return Reason.UserBadPhoneNumber;
                    if (content.contains("Email") || content.contains("email")) return Reason.UserBadEmailAddress;
                    if (content.contains("provide")) return Reason.UserMissingInformation;
                    break;
                case 401:
                    if (content.contains("This endpoint requires a Bearer token")) return Reason.AdminTokenRequired;
                    break;
                case 403:
                    if (content.contains("Invalid token") || content.contains("invalid JWT")) return Reason.AdminTokenRequired;
                    break;
                case 404:
                    if (content.contains("No SSO provider assigned for this domain")) return Reason.SsoDomainNotFound;
                    if (content.contains("No such SSO provider")) return Reason.SsoProviderNotFound;
                    break;
                case 422:
                    if (content.contains("User already registered")) return Reason.UserAlreadyRegistered;
                    if (content.contains("Phone") && content.contains("Email")) return Reason.UserBadMultiple;
                    if (content.contains("email") && content.contains("password")) return Reason.UserBadMultiple;
                    if (content.contains("Password") || content.contains("password")) return Reason.UserBadPassword;
                    break;
                case 429:
                    return Reason.UserTooManyRequests;
                default:
                    return Reason.Unknown;
            }

            return Reason.Unknown;
        }
    }
