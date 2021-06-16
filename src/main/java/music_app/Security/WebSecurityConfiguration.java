package music_app.Security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"music_app.*"})
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private DataSource dataSource;

    private String usersQuery="select email, password, active from user where email=?";
    private String rolesQuery="select u.email, r.role from user u inner join user_role ur on(u.user_id=ur.user_id) inner join role r on(ur.role_id=r.role_id) where u.email=?";


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().usersByUsernameQuery(usersQuery).authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web)  {
        web.ignoring().antMatchers("/resources/**","static/**","static/css/**","static/**/**","templates/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/index/download/**").permitAll()
                .antMatchers("/users/download/**").hasAnyAuthority("ADMIN","User")
                .antMatchers("/Home").hasAnyAuthority("ADMIN","User")
                .antMatchers("/myMusic").hasAnyAuthority("ADMIN","User")
                .antMatchers( "/addMusic/**").hasAnyAuthority("ADMIN","User")
                .antMatchers( "/updateMusic/**").hasAnyAuthority("ADMIN","User")
                .antMatchers( "/table").hasAuthority("ADMIN")
                .antMatchers( "/users").hasAuthority("ADMIN")
                .antMatchers( "/delete/user/**").hasAuthority("ADMIN")
                .antMatchers( "/check/**").hasAuthority("ADMIN")
                .antMatchers( "/delete/music/**").hasAuthority("ADMIN")
                .antMatchers( "/profile/**").hasAuthority("ADMIN")
                .and().csrf().disable().formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
                .defaultSuccessUrl("/index")
                .usernameParameter("email")
                .passwordParameter("password")
                .and()
                .logout().logoutSuccessUrl("/index")
                .and().httpBasic();



    }



}