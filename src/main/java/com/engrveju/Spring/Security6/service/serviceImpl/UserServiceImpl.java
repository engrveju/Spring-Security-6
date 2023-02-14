package com.engrveju.Spring.Security6.service.serviceImpl;

import com.engrveju.Spring.Security6.pojos.AuthenticationRequest;
import com.engrveju.Spring.Security6.pojos.RegisterRequest;
import com.engrveju.Spring.Security6.config.security.JwtService;
import com.engrveju.Spring.Security6.repository.UserRepository;
import com.engrveju.Spring.Security6.service.EmailService;
import com.engrveju.Spring.Security6.service.UserService;
import com.engrveju.Spring.Security6.enums.Role;
import com.engrveju.Spring.Security6.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
        private final AuthenticationManager authenticationManager;
        private final EmailService emailService;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        //LOGIN SERVICE
        @Override
        public ResponseEntity<?> userLogin(AuthenticationRequest loginRequest) {

            Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                String jwtToken = jwtService.generateToken(user);

                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
                );
                return new ResponseEntity<>(jwtToken, HttpStatus.OK);
            }
            return new ResponseEntity<>("User not found",HttpStatus.BAD_REQUEST);
        }
        //REGISTRATION SERVICE
        @Override
        public ResponseEntity<?> registerUser(RegisterRequest signUpRequest) {
            Optional<User> user = userRepository.findByEmail(signUpRequest.getEmail());
            if (user.isPresent()) {
                if(!user.get().isAccountNonLocked())
                    return new ResponseEntity<>(( "Please confirm your mail!"), HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(( "Email Address already in use!"), HttpStatus.BAD_REQUEST);
            }
            User newUser = User.builder()
                    .firstname(signUpRequest.getFirstname())
                    .lastname(signUpRequest.getLastname())
                    .email(signUpRequest.getEmail())
                    .role(Role.USER)
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .locked(false)
                    .enabled(false)
                    .build();

            String jwtToken = jwtService.generateToken(newUser);
//            newUser.setToken(jwtToken);
            User result = userRepository.save(newUser);

            String link = "http://localhost:8080/api/v1/auth/confirm/" + jwtToken;
            emailService.send(signUpRequest.getEmail(), confirmationEmail(signUpRequest.getFirstname(), link));

            return new ResponseEntity<>("Account created successfully.Check mail  to confirm account",HttpStatus.OK);
        }

        //EMAIL CONFIRMATION SERVICE
      @Override
      @Transactional
     public ResponseEntity<?> confirmAccount(String token){

          if (!jwtService.validateToken(token)) {
              return new ResponseEntity<>("Link Expired or Invalid, Please enter you email to resend link", HttpStatus.ACCEPTED);
          }
          String  userEmail = jwtService.extractUsername(token);

         Optional<User> users = userRepository.findByEmail(userEmail);
         User user;
         if(users.isPresent()) {
             user = users.get();

             if(user.isEnabled()) {
                 return new ResponseEntity<>("Account already Confirmed. Please Login", HttpStatus.ACCEPTED);
             }else{
                 user.setEnabled(true);
             }
     }
         return new ResponseEntity<>("Account Successfully confirmed",HttpStatus.OK);
     }

     //RESEND EMAIL CONFIRMATION SERVICE
    @Override
    public ResponseEntity<?> resendConfirmationMail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if(optionalUser.isPresent()) {
            user = optionalUser.get();
            if (!user.isEnabled()) {
                String jwtToken = jwtService.generateToken(user);
                String link = "http://localhost:8080/api/v1/auth/confirm/" + jwtToken;
                emailService.send(email, confirmationEmail(user.getFirstname(), link));

                return new ResponseEntity<>("Confirmation mail has been resent", HttpStatus.OK);
            }
            return new ResponseEntity<>("Account Already Confirmed",HttpStatus.OK);
        }
        return new ResponseEntity<>("Email does not exist in our database",HttpStatus.BAD_REQUEST);
    }


    public String confirmationEmail(String nameOfUser, String link){
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + nameOfUser + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
