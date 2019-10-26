import me.gorgeousone.tangledmaze.command.BuildCommand;
import me.gorgeousone.tangledmaze.command.MazeCommand;
import me.gorgeousone.tangledmaze.core.Maze;
import me.gorgeousone.tangledmaze.handler.MazeHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BuildCommandTest {

	private static MazeCommand tangledmaze;

	@BeforeEach
	void setUp() {
		tangledmaze = new MazeCommand();
		tangledmaze.addChild(new BuildCommand(tangledmaze));
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testEverything() {

		Player mockPlayer = mock(Player.class);
		when(mockPlayer.hasPermission(anyString())).thenReturn(false);

		Maze mockMaze = mock(Maze.class);
		when(mockMaze.isStarted()).thenReturn(true);
		when(mockMaze.hasExits()).thenReturn(true);

		MazeHandler.setMaze(mockPlayer, mockMaze);

		tangledmaze.execute(mockPlayer, new String[] {"build", "walls", "stone"});

		verify(mockPlayer).sendMessage(ChatColor.RED + "You do not have the permission for this command.");
	}
}