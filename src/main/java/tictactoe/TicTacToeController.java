package tictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tictactoe.game.ComputerPlayerService;
import tictactoe.game.GameService;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.Game.PlayerType;
import tictactoe.user.entity.AppUser;
import tictactoe.user.entity.AppUserRepository;

import java.security.Principal;

@Controller
public class TicTacToeController {

    private final GameService gameService;

    private final ComputerPlayerService computerPlayerService;

    private final AppUserRepository appUserRepository;

    @Autowired
    public TicTacToeController(
            GameService gameService,
            ComputerPlayerService computerPlayerService,
            AppUserRepository appUserRepository) {

        this.gameService = gameService;
        this.computerPlayerService = computerPlayerService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/")
    public String index(Principal principal, Model model) {
        AppUser appUser = getAppUser(principal);

        Game game = gameService.getLastGame(appUser);
        if (game == null) {
            game = gameService.create(appUser, true);
        }

        setModelGameAttributes(model, game);

        return "index";
    }

    @PostMapping(value = "/")
    public String takeTurns(
            Model model,
            Principal principal,
            @RequestParam("tile_id") String tileId,
            @RequestParam(value = "new_game", required = false, defaultValue = "false") boolean newGame,
            @RequestParam(value = "player_go_first", required = false, defaultValue = "false") boolean playerGoFirst
    ) {
        AppUser appUser = getAppUser(principal);

        Game game;
        if (newGame) {
            game = gameService.create(appUser, playerGoFirst);

            if (!playerGoFirst) {
                // give computer a small advantage by always placing X in the center as its first move
                gameService.takeTurn(game, "1-1");
            }
        } else {
            game = gameService.getLastGame(appUser);
            gameService.takeTurn(game, tileId); // Player Turn
            computerPlayerService.takeTurn(game); // Computer Turn
        }

        setModelGameAttributes(model, game);

        return "index";
    }

    private void setModelGameAttributes(Model model, Game game) {
        boolean playerGoFirst = game.getPlayer1Type() == PlayerType.HUMAN;

        String playerStatus;
        switch (game.getState()) {
            case PLAYER_1_WIN:
                playerStatus = playerGoFirst ? "WON" : "LOST";
                break;
            case PLAYER_2_WIN:
                playerStatus = playerGoFirst ? "LOST" : "WON";
                break;
            case DRAW:
                playerStatus = "DRAW";
                break;
            case IN_PROGRESS:
            default:
                playerStatus = "IN_PROGRESS";
                break;
        }

        model.addAttribute("playerGoFirst", playerGoFirst);
        model.addAttribute("playStatus", playerStatus);
        model.addAttribute("board", game.getRows());
    }

    private AppUser getAppUser(Principal principal) {
        AppUser appUser = appUserRepository.findByUsername(principal.getName());
        if (appUser == null) {
            throw new UsernameNotFoundException("Invalid username: " + principal.getName());
        }
        return appUser;
    }
}
